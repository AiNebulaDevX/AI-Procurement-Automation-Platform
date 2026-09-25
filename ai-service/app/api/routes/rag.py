"""
API routes for RAG (Retrieval Augmented Generation) endpoints
"""

from fastapi import APIRouter, HTTPException, UploadFile, File
from typing import Dict, Any, List
import logging
import os

from app.rag.processor import DocumentProcessor
from app.rag.retriever import VectorRetriever
from app.rag.generator import RAGGenerator

router = APIRouter()
logger = logging.getLogger(__name__)

# Initialize RAG components
document_processor = DocumentProcessor()
vector_retriever = VectorRetriever()
rag_generator = RAGGenerator()


@router.post("/upload-document")
async def upload_document(file: UploadFile = File(...)):
    """
    Upload a document for RAG processing
    
    Supported formats: PDF, TXT, DOCX
    """
    try:
        # Ensure upload directory exists
        upload_dir = "documents"
        os.makedirs(upload_dir, exist_ok=True)
        
        # Save uploaded file
        file_path = os.path.join(upload_dir, file.filename)
        content = await file.read()
        
        with open(file_path, 'wb') as f:
            f.write(content)
        
        # Process document
        result = await document_processor.process_document(
            file_path=file_path,
            file_name=file.filename,
            file_type=file.content_type or "application/octet-stream",
            metadata={"original_filename": file.filename}
        )
        
        return result
    except Exception as e:
        logger.error(f"Error uploading document: {e}")
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/query")
async def query_rag(request: Dict[str, Any]):
    """
    Query the RAG system
    
    Request body:
    {
        "query": str,
        "top_k": int (optional, default 5)
    }
    """
    try:
        query = request.get("query")
        top_k = request.get("top_k", 5)
        
        if not query:
            raise HTTPException(status_code=400, detail="query is required")
        
        # Retrieve relevant documents
        retrieved_docs = await vector_retriever.retrieve(query, top_k)
        
        # Generate answer using retrieved context
        answer = await rag_generator.generate_answer(query, retrieved_docs)
        
        return {
            "query": query,
            "answer": answer["answer"],
            "sources": answer["sources"],
            "confidence": answer["confidence"],
            "context_used": answer["context_used"]
        }
    except Exception as e:
        logger.error(f"Error querying RAG: {e}")
        raise HTTPException(status_code=500, detail=str(e))


@router.get("/documents")
async def list_documents():
    """List all documents in the RAG system"""
    try:
        documents = await document_processor.list_documents()
        return {
            "documents": documents,
            "total": len(documents)
        }
    except Exception as e:
        logger.error(f"Error listing documents: {e}")
        raise HTTPException(status_code=500, detail=str(e))


@router.delete("/documents/{document_id}")
async def delete_document(document_id: int):
    """Delete a document from the RAG system"""
    try:
        success = await document_processor.delete_document(document_id)
        if success:
            return {
                "status": "success",
                "message": f"Document {document_id} deleted"
            }
        else:
            raise HTTPException(status_code=404, detail="Document not found")
    except Exception as e:
        logger.error(f"Error deleting document: {e}")
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/similarity-search")
async def similarity_search(request: Dict[str, Any]):
    """
    Perform similarity search without generating an answer
    
    Request body:
    {
        "query": str,
        "top_k": int (optional, default 5),
        "filters": dict (optional)
    }
    """
    try:
        query = request.get("query")
        top_k = request.get("top_k", 5)
        filters = request.get("filters")
        
        if not query:
            raise HTTPException(status_code=400, detail="query is required")
        
        retrieved_docs = await vector_retriever.retrieve(query, top_k, filters)
        
        return {
            "query": query,
            "results": retrieved_docs,
            "total": len(retrieved_docs)
        }
    except Exception as e:
        logger.error(f"Error performing similarity search: {e}")
        raise HTTPException(status_code=500, detail=str(e))
