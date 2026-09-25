from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from contextlib import asynccontextmanager
import logging

from app.api.routes import agent, rag, documents
from app.core.config import settings
from app.core.database import init_db

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup
    logger.info("Starting AI Procurement Agent Service")
    await init_db()
    yield
    # Shutdown
    logger.info("Shutting down AI Procurement Agent Service")


app = FastAPI(
    title="AI Procurement Agent Service",
    description="Enterprise AI Agent for Procurement Automation",
    version="1.0.0",
    lifespan=lifespan
)

# CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:3000", "http://frontend:3000"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Include routers
app.include_router(agent.router, prefix="/agent", tags=["Agent"])
app.include_router(rag.router, prefix="/rag", tags=["RAG"])
app.include_router(documents.router, prefix="/documents", tags=["Documents"])


@app.get("/")
async def root():
    return {
        "service": "AI Procurement Agent Service",
        "version": "1.0.0",
        "status": "running"
    }


@app.get("/health")
async def health_check():
    return {"status": "healthy"}


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=8000,
        reload=True
    )
