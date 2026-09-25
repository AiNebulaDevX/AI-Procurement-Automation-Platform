"""
API routes for AI Agent endpoints
"""

from fastapi import APIRouter, HTTPException
from typing import Dict, Any
import logging

from app.agent.agent import ProcurementAgent

router = APIRouter()
logger = logging.getLogger(__name__)

# Global agent instance
agent = ProcurementAgent()


@router.post("/analyze")
async def analyze_quotation(request: Dict[str, Any]) -> Dict[str, Any]:
    """
    Analyze a quotation using AI
    
    Request body:
    {
        "quotation_id": int,
        "quotation_data": dict (optional)
    }
    """
    try:
        quotation_id = request.get("quotation_id")
        quotation_data = request.get("quotation_data")
        
        if not quotation_id:
            raise HTTPException(status_code=400, detail="quotation_id is required")
        
        result = await agent.analyze_quotation(quotation_id, quotation_data)
        return result
    except Exception as e:
        logger.error(f"Error analyzing quotation: {e}")
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/recommend")
async def get_vendor_recommendation(request: Dict[str, Any]) -> Dict[str, Any]:
    """
    Get vendor recommendation using AI agent
    
    Request body:
    {
        "product_description": str,
        "quantity": int,
        "budget": float,
        "requirements": str (optional)
    }
    """
    try:
        product_description = request.get("product_description")
        quantity = request.get("quantity")
        budget = request.get("budget")
        requirements = request.get("requirements", "")
        
        if not all([product_description, quantity, budget]):
            raise HTTPException(
                status_code=400,
                detail="product_description, quantity, and budget are required"
            )
        
        result = await agent.analyze_vendor_selection(
            product_description,
            quantity,
            budget,
            requirements
        )
        return result
    except Exception as e:
        logger.error(f"Error getting vendor recommendation: {e}")
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/generate-negotiation-email")
async def generate_negotiation_email(request: Dict[str, Any]) -> Dict[str, Any]:
    """
    Generate a negotiation email using AI
    
    Request body:
    {
        "vendor_name": str,
        "product_description": str,
        "current_price": float,
        "target_price": float,
        "historical_price": float,
        "reasoning": str
    }
    """
    try:
        vendor_name = request.get("vendor_name")
        product_description = request.get("product_description")
        current_price = request.get("current_price")
        target_price = request.get("target_price")
        historical_price = request.get("historical_price")
        reasoning = request.get("reasoning")
        
        if not all([vendor_name, product_description, current_price, target_price]):
            raise HTTPException(
                status_code=400,
                detail="vendor_name, product_description, current_price, and target_price are required"
            )
        
        result = await agent.generate_negotiation_email(
            vendor_name,
            product_description,
            current_price,
            target_price,
            historical_price or 0,
            reasoning or ""
        )
        return result
    except Exception as e:
        logger.error(f"Error generating negotiation email: {e}")
        raise HTTPException(status_code=500, detail=str(e))


@router.get("/health")
async def health_check():
    """Health check endpoint"""
    return {"status": "healthy", "service": "AI Agent"}
