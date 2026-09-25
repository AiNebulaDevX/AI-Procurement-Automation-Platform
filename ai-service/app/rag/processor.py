"""
Document processing for RAG system
"""

import logging
import aiofiles
import os
from typing import List, Dict, Any, Optional
from datetime import datetime

from app.rag.chunker import TextChunker
from app.rag.embeddings import EmbeddingService
from app.core.database import get_db
from sqlalchemy import text
from app.core.config import settings

logger = logging.getLogger(__name__)


class DocumentProcessor:
    """Service for processing documents and storing in RAG system"""
    
    def __init__(self):
        self.chunker = TextChunker(chunk_size=1000, chunk_overlap=200)
        self.embedding_service = EmbeddingService()
        self.upload_dir = "documents"
        
        # Ensure upload directory exists
        os.makedirs(self.upload_dir, exist_ok=True)
    
    async def process_document(
        self,
        file_path: str,
        file_name: str,
        file_type: str,
        metadata: Optional[Dict[str, Any]] = None
    ) -> Dict[str, Any]:
        """
        Process a document and store in RAG system
        
        Args:
            file_path: Path to the document file
            file_name: Name of the file
            file_type: MIME type of the file
            metadata: Optional metadata
        
        Returns:
            Processing result with document ID
        """
        try:
            # Extract text from document
            text = await self._extract_text(file_path, file_type)
            
            if not text:
                raise ValueError("No text extracted from document")
            
            # Chunk the text
            chunks = self.chunker.chunk_text(text)
            
            # Generate embeddings for chunks
            embeddings = await self.embedding_service.generate_embeddings(chunks)
            
            # Store document metadata in database
            document_id = await self._store_document(file_name, file_path, file_type, metadata)
            
            # Store chunks and embeddings
            await self._store_chunks(document_id, chunks, embeddings, metadata)
            
            return {
                "status": "success",
                "document_id": document_id,
                "chunks_processed": len(chunks),
                "file_name": file_name
            }
            
        except Exception as e:
            logger.error(f"Error processing document: {e}")
            return {
                "status": "error",
                "error": str(e)
            }
    
    async def _extract_text(self, file_path: str, file_type: str) -> str:
        """Extract text from document based on file type"""
        if file_type == "application/pdf":
            return await self._extract_pdf_text(file_path)
        elif file_type == "text/plain":
            return await self._extract_plain_text(file_path)
        else:
            # Default to plain text extraction
            return await self._extract_plain_text(file_path)
    
    async def _extract_pdf_text(self, file_path: str) -> str:
        """Extract text from PDF file"""
        try:
            import pdfplumber
            
            text = ""
            with pdfplumber.open(file_path) as pdf:
                for page in pdf.pages:
                    text += page.extract_text() + "\n"
            
            return text
        except Exception as e:
            logger.error(f"Error extracting PDF text: {e}")
            # Fallback to simple text extraction
            return await self._extract_plain_text(file_path)
    
    async def _extract_plain_text(self, file_path: str) -> str:
        """Extract plain text from file"""
        try:
            async with aiofiles.open(file_path, 'r', encoding='utf-8') as f:
                return await f.read()
        except Exception as e:
            logger.error(f"Error extracting plain text: {e}")
            return ""
    
    async def _store_document(
        self,
        file_name: str,
        file_path: str,
        file_type: str,
        metadata: Optional[Dict[str, Any]]
    ) -> int:
        """Store document metadata in database"""
        async for session in get_db():
            try:
                import json
                metadata_json = json.dumps(metadata) if metadata else None
                
                sql = """
                INSERT INTO documents (file_name, file_path, file_type, metadata, created_at)
                VALUES (:file_name, :file_path, :file_type, :metadata, :created_at)
                RETURNING id
                """
                
                result = await session.execute(
                    text(sql),
                    {
                        "file_name": file_name,
                        "file_path": file_path,
                        "file_type": file_type,
                        "metadata": metadata_json,
                        "created_at": datetime.utcnow()
                    }
                )
                
                document_id = result.scalar()
                await session.commit()
                
                return document_id
                
            except Exception as e:
                logger.error(f"Error storing document: {e}")
                await session.rollback()
                raise
    
    async def _store_chunks(
        self,
        document_id: int,
        chunks: List[str],
        embeddings: List[List[float]],
        metadata: Optional[Dict[str, Any]]
    ):
        """Store chunks and embeddings in database"""
        async for session in get_db():
            try:
                import json
                metadata_json = json.dumps(metadata) if metadata else None
                
                for chunk, embedding in zip(chunks, embeddings):
                    # Convert embedding to pgvector format
                    embedding_str = '[' + ','.join(map(str, embedding)) + ']'
                    
                    sql = """
                    INSERT INTO embeddings (document_id, chunk_text, embedding, metadata, created_at)
                    VALUES (:document_id, :chunk_text, :embedding::vector, :metadata, :created_at)
                    """
                    
                    await session.execute(
                        text(sql),
                        {
                            "document_id": document_id,
                            "chunk_text": chunk,
                            "embedding": embedding_str,
                            "metadata": metadata_json,
                            "created_at": datetime.utcnow()
                        }
                    )
                
                await session.commit()
                
            except Exception as e:
                logger.error(f"Error storing chunks: {e}")
                await session.rollback()
                raise
    
    async def delete_document(self, document_id: int) -> bool:
        """Delete a document and its chunks from the RAG system"""
        async for session in get_db():
            try:
                # Delete embeddings first (foreign key constraint)
                await session.execute(
                    text("DELETE FROM embeddings WHERE document_id = :document_id"),
                    {"document_id": document_id}
                )
                
                # Delete document
                await session.execute(
                    text("DELETE FROM documents WHERE id = :document_id"),
                    {"document_id": document_id}
                )
                
                await session.commit()
                return True
                
            except Exception as e:
                logger.error(f"Error deleting document: {e}")
                await session.rollback()
                return False
    
    async def list_documents(self) -> List[Dict[str, Any]]:
        """List all documents in the RAG system"""
        async for session in get_db():
            try:
                sql = """
                SELECT id, file_name, file_type, created_at
                FROM documents
                ORDER BY created_at DESC
                """
                
                result = await session.execute(text(sql))
                rows = result.fetchall()
                
                documents = []
                for row in rows:
                    documents.append({
                        "id": row[0],
                        "file_name": row[1],
                        "file_type": row[2],
                        "created_at": row[3].isoformat() if row[3] else None
                    })
                
                return documents
                
            except Exception as e:
                logger.error(f"Error listing documents: {e}")
                return []
