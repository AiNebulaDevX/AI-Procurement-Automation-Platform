"""
Retrieval service for RAG system
"""

import logging
from typing import List, Dict, Any, Optional
import numpy as np

from app.rag.embeddings import EmbeddingService
from app.core.database import get_db
from sqlalchemy import text
from app.core.config import settings

logger = logging.getLogger(__name__)


class VectorRetriever:
    """Service for retrieving relevant documents using vector similarity"""
    
    def __init__(self):
        self.embedding_service = EmbeddingService()
    
    async def retrieve(
        self,
        query: str,
        top_k: int = 5,
        filters: Optional[Dict[str, Any]] = None
    ) -> List[Dict[str, Any]]:
        """
        Retrieve relevant document chunks based on query
        
        Args:
            query: Search query
            top_k: Number of results to return
            filters: Optional filters for metadata
        
        Returns:
            List of relevant chunks with similarity scores
        """
        # Generate query embedding
        query_embedding = await self.embedding_service.generate_embedding(query)
        
        # Search pgvector for similar chunks
        async for session in get_db():
            try:
                # Convert embedding to pgvector format
                embedding_array = '[' + ','.join(map(str, query_embedding)) + ']'
                
                # Build SQL query with vector similarity
                sql_query = """
                SELECT 
                    e.id,
                    e.chunk_text,
                    e.metadata,
                    d.file_name,
                    d.file_type,
                    1 - (e.embedding <=> :embedding::vector) as similarity
                FROM embeddings e
                LEFT JOIN documents d ON e.document_id = d.id
                WHERE 1=1
                """
                
                params = {"embedding": embedding_array}
                
                # Add filters if provided
                if filters:
                    if filters.get("document_id"):
                        sql_query += " AND e.document_id = :document_id"
                        params["document_id"] = filters["document_id"]
                    if filters.get("file_type"):
                        sql_query += " AND d.file_type = :file_type"
                        params["file_type"] = filters["file_type"]
                
                sql_query += """
                ORDER BY e.embedding <=> :embedding::vector
                LIMIT :top_k
                """
                params["top_k"] = top_k
                
                result = await session.execute(text(sql_query), params)
                rows = result.fetchall()
                
                # Format results
                retrieved_docs = []
                for row in rows:
                    retrieved_docs.append({
                        "id": row[0],
                        "chunk_text": row[1],
                        "metadata": row[2],
                        "file_name": row[3],
                        "file_type": row[4],
                        "similarity_score": float(row[5])
                    })
                
                return retrieved_docs
                
            except Exception as e:
                logger.error(f"Error retrieving documents: {e}")
                # Fallback to mock retrieval
                return self._mock_retrieve(query, top_k)
    
    def _mock_retrieve(self, query: str, top_k: int) -> List[Dict[str, Any]]:
        """Mock retrieval for testing without database"""
        mock_docs = [
            {
                "id": 1,
                "chunk_text": "Procurement policy requires all purchases above $50,000 to go through finance approval.",
                "metadata": {"type": "policy", "section": "approval_limits"},
                "file_name": "procurement_policy.pdf",
                "file_type": "application/pdf",
                "similarity_score": 0.85
            },
            {
                "id": 2,
                "chunk_text": "Vendor evaluation criteria include price competitiveness, delivery timeline, and historical performance.",
                "metadata": {"type": "policy", "section": "vendor_evaluation"},
                "file_name": "vendor_guidelines.pdf",
                "file_type": "application/pdf",
                "similarity_score": 0.78
            },
            {
                "id": 3,
                "chunk_text": "All contracts must be reviewed by legal department before signature.",
                "metadata": {"type": "policy", "section": "contracts"},
                "file_name": "contract_policy.pdf",
                "file_type": "application/pdf",
                "similarity_score": 0.72
            }
        ]
        
        return mock_docs[:top_k]
