"""
Text chunking for document processing
"""

import logging
from typing import List
import re

logger = logging.getLogger(__name__)


class TextChunker:
    """Service for chunking text into smaller segments"""
    
    def __init__(self, chunk_size: int = 1000, chunk_overlap: int = 200):
        self.chunk_size = chunk_size
        self.chunk_overlap = chunk_overlap
    
    def chunk_text(self, text: str) -> List[str]:
        """Split text into overlapping chunks"""
        if not text:
            return []
        
        # Clean the text
        text = self._clean_text(text)
        
        # Split by paragraphs first
        paragraphs = self._split_by_paragraphs(text)
        
        chunks = []
        current_chunk = ""
        
        for paragraph in paragraphs:
            # If adding this paragraph would exceed chunk size
            if len(current_chunk) + len(paragraph) > self.chunk_size:
                if current_chunk:
                    chunks.append(current_chunk.strip())
                
                # Start new chunk with overlap
                if self.chunk_overlap > 0 and chunks:
                    overlap_text = chunks[-1][-self.chunk_overlap:]
                    current_chunk = overlap_text + "\n\n" + paragraph
                else:
                    current_chunk = paragraph
            else:
                if current_chunk:
                    current_chunk += "\n\n" + paragraph
                else:
                    current_chunk = paragraph
        
        # Add the last chunk
        if current_chunk:
            chunks.append(current_chunk.strip())
        
        return chunks
    
    def _clean_text(self, text: str) -> str:
        """Clean and normalize text"""
        # Remove excessive whitespace
        text = re.sub(r'\s+', ' ', text)
        # Remove special characters that might interfere
        text = re.sub(r'[\x00-\x08\x0b-\x0c\x0e-\x1f\x7f-\x9f]', '', text)
        return text.strip()
    
    def _split_by_paragraphs(self, text: str) -> List[str]:
        """Split text into paragraphs"""
        # Split by common paragraph delimiters
        paragraphs = re.split(r'\n\n+|\r\n\r\n+', text)
        
        # Filter out empty paragraphs
        paragraphs = [p.strip() for p in paragraphs if p.strip()]
        
        # If no paragraphs found, split by sentences
        if not paragraphs:
            sentences = re.split(r'[.!?]+', text)
            paragraphs = [s.strip() for s in sentences if s.strip()]
        
        return paragraphs
    
    def chunk_by_structure(self, text: str, structure_markers: List[str] = None) -> List[str]:
        """Chunk text based on structural markers (headers, sections, etc.)"""
        if structure_markers is None:
            structure_markers = ['\n##', '\n###', '\n####', 'CHAPTER', 'SECTION']
        
        chunks = []
        current_chunk = ""
        
        lines = text.split('\n')
        
        for line in lines:
            is_marker = any(marker in line.upper() for marker in structure_markers)
            
            if is_marker and current_chunk:
                chunks.append(current_chunk.strip())
                current_chunk = line + "\n"
            else:
                current_chunk += line + "\n"
        
        if current_chunk:
            chunks.append(current_chunk.strip())
        
        return chunks
