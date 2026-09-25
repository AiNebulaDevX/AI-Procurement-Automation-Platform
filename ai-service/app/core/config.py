from pydantic_settings import BaseSettings
from typing import Optional


class Settings(BaseSettings):
    # Database
    DATABASE_URL: str = "postgresql://procurement_user:procurement_pass@localhost:5432/procurement_db"
    
    # Redis
    REDIS_URL: str = "redis://localhost:6379"
    
    # Kafka
    KAFKA_BOOTSTRAP_SERVERS: str = "localhost:9092"
    
    # OpenAI
    OPENAI_API_KEY: Optional[str] = None
    LLM_MODEL: str = "gpt-4"
    EMBEDDING_MODEL: str = "text-embedding-ada-002"
    
    # Application
    APP_NAME: str = "AI Procurement Agent Service"
    DEBUG: bool = True
    
    class Config:
        env_file = ".env"
        case_sensitive = True


settings = Settings()
