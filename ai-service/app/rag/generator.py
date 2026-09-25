"""
RAG Generator - Uses retrieved context to generate answers
"""

import logging
from typing import List, Dict, Any

from langchain_openai import ChatOpenAI
from langchain.prompts import ChatPromptTemplate
from langchain.schema import HumanMessage, SystemMessage

from app.core.config import settings

logger = logging.getLogger(__name__)


class RAGGenerator:
    """Service for generating answers using retrieved context"""
    
    def __init__(self):
        if settings.OPENAI_API_KEY:
            self.llm = ChatOpenAI(
                model=settings.LLM_MODEL,
                temperature=0.3,
                openai_api_key=settings.OPENAI_API_KEY
            )
        else:
            logger.warning("OpenAI API key not set, using mock generator")
            self.llm = None
    
    async def generate_answer(
        self,
        query: str,
        context: List[Dict[str, Any]],
        max_tokens: int = 500
    ) -> Dict[str, Any]:
        """
        Generate an answer using retrieved context
        
        Args:
            query: User's question
            context: Retrieved document chunks
            max_tokens: Maximum tokens in response
        
        Returns:
            Generated answer with sources
        """
        if not context:
            return {
                "answer": "I don't have relevant information to answer this question based on the available documents.",
                "sources": [],
                "confidence": 0.0
            }
        
        # Build context string
        context_text = self._build_context_string(context)
        
        # Generate answer
        if self.llm:
            answer = await self._generate_with_llm(query, context_text)
        else:
            answer = self._generate_mock_answer(query, context)
        
        # Extract sources
        sources = [
            {
                "file_name": doc.get("file_name", "Unknown"),
                "chunk_id": doc.get("id"),
                "similarity": doc.get("similarity_score", 0.0)
            }
            for doc in context
        ]
        
        # Calculate confidence based on similarity scores
        avg_similarity = sum(doc.get("similarity_score", 0.0) for doc in context) / len(context)
        confidence = min(avg_similarity * 100, 100.0)
        
        return {
            "answer": answer,
            "sources": sources,
            "confidence": round(confidence, 2),
            "context_used": len(context)
        }
    
    def _build_context_string(self, context: List[Dict[str, Any]]) -> str:
        """Build a formatted context string from retrieved chunks"""
        context_parts = []
        for i, doc in enumerate(context, 1):
            context_parts.append(
                f"Document {i} ({doc.get('file_name', 'Unknown')}):\n{doc.get('chunk_text', '')}"
            )
        return "\n\n".join(context_parts)
    
    async def _generate_with_llm(self, query: str, context: str) -> str:
        """Generate answer using LLM"""
        try:
            prompt = ChatPromptTemplate.from_messages([
                SystemMessage(content="""You are a helpful assistant that answers questions based on the provided context. 
                Use only the information from the context to answer. If the answer is not in the context, say so.
                Be concise and accurate."""),
                HumanMessage(content=f"""Context:
{context}

Question: {query}

Answer:""")
            ])
            
            response = await self.llm.ainvoke(prompt)
            return response.content
        except Exception as e:
            logger.error(f"Error generating answer with LLM: {e}")
            return self._generate_mock_answer(query, [])
    
    def _generate_mock_answer(self, query: str, context: List[Dict[str, Any]]) -> str:
        """Generate a mock answer for testing without LLM"""
        if not context:
            return "Based on the available documents, I cannot find relevant information to answer your question."
        
        # Simple keyword-based answer generation
        query_lower = query.lower()
        relevant_chunks = []
        
        for doc in context:
            chunk_text = doc.get("chunk_text", "").lower()
            if any(word in chunk_text for word in query_lower.split()):
                relevant_chunks.append(doc.get("chunk_text", ""))
        
        if relevant_chunks:
            return f"Based on the procurement documents: {relevant_chunks[0][:200]}..."
        
        return f"Based on the retrieved documents, here's what I found: {context[0].get('chunk_text', '')[:200]}..."
