"""
Embedding generation and management for RAG system
"""

import logging
from typing import List, Optional
import numpy as np

from langchain_openai import OpenAIEmbeddings
from app.core.config import settings

logger = logging.getLogger(__name__)


class EmbeddingService:
    """Service for generating and managing embeddings"""
    
    def __init__(self):
        if settings.OPENAI_API_KEY:
            self.embeddings = OpenAIEmbeddings(
                model=settings.EMBEDDING_MODEL,
                openai_api_key=settings.OPENAI_API_KEY
            )
        else:
            logger.warning("OpenAI API key not set, using mock embeddings")
            self.embeddings = None
    
    async def generate_embedding(self, text: str) -> List[float]:
        """Generate embedding for a single text"""
        if self.embeddings:
            try:
                embedding = await self.embeddings.aembed_query(text)
                return embedding
            except Exception as e:
                logger.error(f"Error generating embedding: {e}")
                return self._mock_embedding(text)
        else:
            return self._mock_embedding(text)
    
    async def generate_embeddings(self, texts: List[str]) -> List[List[float]]:
        """Generate embeddings for multiple texts"""
        if self.embeddings:
            try:
                embeddings = await self.embeddings.aembed_documents(texts)
                return embeddings
            except Exception as e:
                logger.error(f"Error generating embeddings: {e}")
                return [self._mock_embedding(text) for text in texts]
        else:
            return [self._mock_embedding(text) for text in texts]
    
    def _mock_embedding(self, text: str) -> List[float]:
        """Generate a mock embedding for testing without API key"""
        # Simple hash-based embedding for testing
        import hashlib
        hash_obj = hashlib.md5(text.encode())
        hash_hex = hash_obj.hexdigest()
        
        # Convert to 1536-dimensional vector (OpenAI default)
        embedding = []
        for i in range(1536):
            byte_val = int(hash_hex[i % len(hash_hex)], 16)
            normalized = (byte_val / 255.0 - 0.5) * 2
            embedding.append(normalized)
        
        return embedding
    
    def cosine_similarity(self, vec1: List[float], vec2: List[float]) -> float:
        """Calculate cosine similarity between two vectors"""
        vec1_np = np.array(vec1)
        vec2_np = np.array(vec2)
        
        dot_product = np.dot(vec1_np, vec2_np)
        norm1 = np.linalg.norm(vec1_np)
        norm2 = np.linalg.norm(vec2_np)
        
        if norm1 == 0 or norm2 == 0:
            return 0.0
        
        return dot_product / (norm1 * norm2)
