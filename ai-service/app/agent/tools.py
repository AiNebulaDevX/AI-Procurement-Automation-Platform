"""
Tools for the AI Procurement Agent
These are callable functions that the agent can use to interact with the system
"""

import httpx
import json
import logging
from typing import Dict, Any, List
from app.core.config import settings

logger = logging.getLogger(__name__)


class ProcurementTools:
    """Collection of procurement-related tools for the AI agent"""
    
    def __init__(self, backend_url: str = "http://backend:8080"):
        self.backend_url = backend_url
        self.client = httpx.AsyncClient(timeout=30.0)
    
    async def get_vendors(self) -> List[Dict[str, Any]]:
        """Get all vendors from the system"""
        try:
            response = await self.client.get(f"{self.backend_url}/api/vendors")
            if response.status_code == 200:
                return response.json()
            return []
        except Exception as e:
            logger.error(f"Error getting vendors: {e}")
            return []
    
    async def get_vendor_by_id(self, vendor_id: int) -> Dict[str, Any]:
        """Get a specific vendor by ID"""
        try:
            response = await self.client.get(f"{self.backend_url}/api/vendors/{vendor_id}")
            if response.status_code == 200:
                return response.json()
            return {}
        except Exception as e:
            logger.error(f"Error getting vendor {vendor_id}: {e}")
            return {}
    
    async def get_purchase_history(self, product_name: str = None) -> List[Dict[str, Any]]:
        """Get purchase history, optionally filtered by product"""
        try:
            response = await self.client.get(f"{self.backend_url}/api/procurement/purchase-orders")
            if response.status_code == 200:
                orders = response.json()
                if product_name:
                    # Filter by product name (simplified)
                    orders = [o for o in orders if product_name.lower() in str(o).lower()]
                return orders
            return []
        except Exception as e:
            logger.error(f"Error getting purchase history: {e}")
            return []
    
    async def get_quotations(self, status: str = None) -> List[Dict[str, Any]]:
        """Get quotations, optionally filtered by status"""
        try:
            url = f"{self.backend_url}/api/procurement/quotations"
            if status:
                url += f"/status/{status}"
            response = await self.client.get(url)
            if response.status_code == 200:
                return response.json()
            return []
        except Exception as e:
            logger.error(f"Error getting quotations: {e}")
            return []
    
    async def analyze_quotation(self, quotation_id: int) -> Dict[str, Any]:
        """Analyze a specific quotation"""
        try:
            response = await self.client.get(f"{self.backend_url}/api/procurement/quotations/{quotation_id}")
            if response.status_code == 200:
                return response.json()
            return {}
        except Exception as e:
            logger.error(f"Error analyzing quotation {quotation_id}: {e}")
            return {}
    
    async def calculate_price_anomaly(self, current_price: float, historical_avg: float) -> Dict[str, Any]:
        """Calculate if current price is anomalous compared to historical average"""
        if historical_avg == 0:
            return {
                "is_anomaly": False,
                "percentage_diff": 0,
                "reason": "No historical data available"
            }
        
        percentage_diff = ((current_price - historical_avg) / historical_avg) * 100
        is_anomaly = abs(percentage_diff) > 15  # 15% threshold
        
        return {
            "is_anomaly": is_anomaly,
            "percentage_diff": round(percentage_diff, 2),
            "threshold": 15,
            "reason": f"Price differs by {percentage_diff:.2f}% from historical average"
        }
    
    async def score_vendor(self, vendor: Dict[str, Any], criteria: Dict[str, Any]) -> Dict[str, Any]:
        """Score a vendor based on multiple criteria"""
        score = 0
        max_score = 100
        factors = []
        
        rating_weight = 0.3
        if vendor.get("rating"):
            rating_score = (vendor["rating"] / 5.0) * 30
            score += rating_score
            factors.append(f"Rating: {rating_score:.1f}/30")
        
        # Price competitiveness (30%)
        if criteria.get("target_price"):
            price_score = 30  # Simplified - would need actual comparison
            score += price_score
            factors.append(f"Price: {price_score}/30")
        
        # Delivery timeline (20%)
        if criteria.get("delivery_days"):
            delivery_score = 20  # Simplified
            score += delivery_score
            factors.append(f"Delivery: {delivery_score}/20")
        
        # Historical performance (20%)
        if vendor.get("total_orders"):
            performance_score = min(20, vendor["total_orders"] / 2)
            score += performance_score
            factors.append(f"Historical: {performance_score:.1f}/20")
        
        return {
            "vendor_id": vendor.get("id"),
            "vendor_name": vendor.get("name"),
            "total_score": round(score, 2),
            "max_score": max_score,
            "factors": factors,
            "rating": vendor.get("rating"),
            "total_orders": vendor.get("total_orders")
        }
    
    async def close(self):
        """Close the HTTP client"""
        await self.client.aclose()
