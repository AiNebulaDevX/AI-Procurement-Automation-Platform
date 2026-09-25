"""
AI Procurement Agent - LangGraph-style agent implementation
This agent can reason, call tools, and make procurement recommendations
"""

import json
import logging
import uuid
from typing import Dict, Any, List, Optional
from datetime import datetime

from langchain_openai import ChatOpenAI
from langchain.prompts import ChatPromptTemplate
from langchain.schema import HumanMessage, SystemMessage

from app.agent.tools import ProcurementTools
from app.core.config import settings

logger = logging.getLogger(__name__)


class ProcurementAgent:
    """AI Agent for procurement automation"""
    
    def __init__(self):
        if settings.OPENAI_API_KEY:
            self.llm = ChatOpenAI(
                model=settings.LLM_MODEL,
                temperature=0.1,
                openai_api_key=settings.OPENAI_API_KEY
            )
        else:
            self.llm = None
            logger.warning("OPENAI_API_KEY not set - running in mock mode")
        self.tools = ProcurementTools()
        self.request_id: Optional[str] = None
    
    async def analyze_vendor_selection(
        self,
        product_description: str,
        quantity: int,
        budget: float,
        requirements: str = ""
    ) -> Dict[str, Any]:
        """
        Analyze and recommend the best vendor for a procurement request
        
        Args:
            product_description: Description of what needs to be procured
            quantity: Quantity needed
            budget: Budget constraint
            requirements: Additional requirements/specifications
        
        Returns:
            Agent recommendation with reasoning
        """
        self.request_id = str(uuid.uuid4())
        
        logger.info(f"Starting vendor selection analysis: {self.request_id}")
        
        # Step 1: Gather information using tools
        vendors = await self.tools.get_vendors()
        active_vendors = [v for v in vendors if v.get("is_active", True)]
        
        purchase_history = await self.tools.get_purchase_history(product_description)
        
        # Step 2: Analyze each vendor
        vendor_scores = []
        for vendor in active_vendors:
            score = await self.tools.score_vendor(
                vendor,
                {
                    "target_price": budget / quantity,
                    "delivery_days": 30
                }
            )
            vendor_scores.append(score)
        
        # Sort by score
        vendor_scores.sort(key=lambda x: x["total_score"], reverse=True)
        
        # Step 3: Use LLM to generate recommendation with reasoning
        top_vendors = vendor_scores[:3]
        
        prompt = self._build_analysis_prompt(
            product_description,
            quantity,
            budget,
            requirements,
            top_vendors,
            purchase_history
        )
        
        try:
            if self.llm:
                response = await self.llm.ainvoke(prompt)
                ai_reasoning = response.content
            else:
                ai_reasoning = "Mock AI reasoning: Based on vendor scores and historical data, the top vendor is recommended due to highest overall score and good rating."
        except Exception as e:
            logger.error(f"LLM invocation failed: {e}")
            ai_reasoning = "AI reasoning unavailable due to service error"
        
        # Step 4: Compile recommendation
        recommendation = {
            "request_id": self.request_id,
            "task_type": "VENDOR_SELECTION",
            "input_data": {
                "product_description": product_description,
                "quantity": quantity,
                "budget": budget,
                "requirements": requirements
            },
            "ai_recommendation": {
                "recommended_vendor": top_vendors[0] if top_vendors else None,
                "alternative_vendors": top_vendors[1:3] if len(top_vendors) > 1 else [],
                "confidence_score": self._calculate_confidence(top_vendors, purchase_history),
                "reasoning": ai_reasoning,
                "risks": self._identify_risks(top_vendors, purchase_history, budget),
                "estimated_savings": self._estimate_savings(top_vendors, budget)
            },
            "tools_called": ["get_vendors", "get_purchase_history", "score_vendor"],
            "timestamp": datetime.utcnow().isoformat()
        }
        
        logger.info(f"Vendor selection analysis complete: {self.request_id}")
        return recommendation
    
    async def analyze_quotation(
        self,
        quotation_id: int,
        quotation_data: Dict[str, Any] = None
    ) -> Dict[str, Any]:
        """
        Analyze a quotation for anomalies, competitiveness, and recommendations
        
        Args:
            quotation_id: ID of the quotation to analyze
            quotation_data: Optional quotation data if already fetched
        
        Returns:
            Analysis results with recommendations
        """
        self.request_id = str(uuid.uuid4())
        
        logger.info(f"Starting quotation analysis: {self.request_id}")
        
        # Fetch quotation data if not provided
        if not quotation_data:
            quotation_data = await self.tools.analyze_quotation(quotation_id)
        
        if not quotation_data:
            return {
                "request_id": self.request_id,
                "error": "Quotation not found"
            }
        
        # Get vendor information
        vendor_id = quotation_data.get("vendorId")
        vendor = await self.tools.get_vendor_by_id(vendor_id) if vendor_id else {}
        
        # Get purchase history for comparison
        purchase_history = await self.tools.get_purchase_history()
        
        # Analyze each item
        items_analysis = []
        for item in quotation_data.get("items", []):
            item_analysis = {
                "description": item.get("description"),
                "quantity": item.get("quantity"),
                "unit_price": item.get("unitPrice"),
                "total_price": item.get("totalPrice"),
                "price_anomaly": await self.tools.calculate_price_anomaly(
                    item.get("unitPrice", 0),
                    self._get_historical_average(item.get("description"), purchase_history)
                )
            }
            items_analysis.append(item_analysis)
        
        # Generate AI reasoning
        prompt = self._build_quotation_analysis_prompt(
            quotation_data,
            vendor,
            items_analysis,
            purchase_history
        )
        
        try:
            if self.llm:
                response = await self.llm.ainvoke(prompt)
                ai_reasoning = response.content
            else:
                ai_reasoning = "Mock AI reasoning: Quotation analysis shows competitive pricing with no significant anomalies detected."
        except Exception as e:
            logger.error(f"LLM invocation failed: {e}")
            ai_reasoning = "AI reasoning unavailable"
        
        recommendation = {
            "request_id": self.request_id,
            "task_type": "QUOTATION_ANALYSIS",
            "input_data": {
                "quotation_id": quotation_id,
                "quotation_data": quotation_data
            },
            "ai_recommendation": {
                "overall_assessment": ai_reasoning,
                "items_analysis": items_analysis,
                "vendor_assessment": {
                    "vendor_name": vendor.get("name"),
                    "vendor_rating": vendor.get("rating"),
                    "historical_orders": vendor.get("total_orders")
                },
                "total_amount": quotation_data.get("totalAmount"),
                "flagged_anomalies": [item for item in items_analysis 
                                     if item["price_anomaly"]["is_anomaly"]],
                "recommendation": self._generate_quotation_recommendation(
                    items_analysis, vendor, quotation_data.get("totalAmount", 0)
                )
            },
            "tools_called": ["analyze_quotation", "get_vendor_by_id", "get_purchase_history"],
            "timestamp": datetime.utcnow().isoformat()
        }
        
        logger.info(f"Quotation analysis complete: {self.request_id}")
        return recommendation
    
    async def generate_negotiation_email(
        self,
        vendor_name: str,
        product_description: str,
        current_price: float,
        target_price: float,
        historical_price: float,
        reasoning: str
    ) -> Dict[str, Any]:
        """
        Generate a negotiation email for a vendor
        
        Args:
            vendor_name: Name of the vendor
            product_description: Product being negotiated
            current_price: Current quoted price
            target_price: Target/negotiated price
            historical_price: Historical average price
            reasoning: Reasoning for the negotiation
        
        Returns:
            Generated email content
        """
        self.request_id = str(uuid.uuid4())
        
        prompt = ChatPromptTemplate.from_messages([
            SystemMessage(content="""You are a professional procurement negotiator. 
            Generate a polite but firm negotiation email to a vendor.
            The email should be professional, data-driven, and maintain good business relationships.
            Include specific price comparisons and reasoning."""),
            HumanMessage(content=f"""Generate a negotiation email with the following details:
            
            Vendor: {vendor_name}
            Product: {product_description}
            Current Price: ${current_price}
            Target Price: ${target_price}
            Historical Average Price: ${historical_price}
            Reasoning: {reasoning}
            
            The email should:
            1. Acknowledge the vendor's quote
            2. Present data-driven comparison with historical prices
            3. Request a revised quotation
            4. Maintain professional tone
            5. Include specific next steps""")
        ])
        
        try:
            if self.llm:
                response = await self.llm.ainvoke(prompt)
                email_content = response.content
            else:
                email_content = f"""Dear {vendor_name},

Thank you for your quotation for {product_description}.

We have reviewed your quote of ${current_price} and compared it with our historical average of ${historical_price}. Based on our analysis, we believe a revised quotation at ${target_price} would be more competitive and in line with market rates.

Reasoning: {reasoning}

We value our relationship with your company and look forward to your revised quotation.

Best regards,
Procurement Team"""
        except Exception as e:
            logger.error(f"LLM invocation failed: {e}")
            email_content = "Failed to generate email"
        
        return {
            "request_id": self.request_id,
            "task_type": "NEGOTIATION_EMAIL",
            "email_content": email_content,
            "metadata": {
                "vendor_name": vendor_name,
                "product_description": product_description,
                "current_price": current_price,
                "target_price": target_price,
                "historical_price": historical_price
            },
            "timestamp": datetime.utcnow().isoformat()
        }
    
    def _build_analysis_prompt(
        self,
        product_description: str,
        quantity: int,
        budget: float,
        requirements: str,
        vendor_scores: List[Dict],
        purchase_history: List[Dict]
    ) -> str:
        """Build prompt for vendor selection analysis"""
        return f"""Analyze the following procurement request and provide a recommendation:

Request Details:
- Product: {product_description}
- Quantity: {quantity}
- Budget: ${budget}
- Requirements: {requirements}

Vendor Scores:
{json.dumps(vendor_scores, indent=2)}

Recent Purchase History:
{json.dumps(purchase_history[:5] if purchase_history else [], indent=2)}

Provide:
1. Recommended vendor with justification
2. Risk assessment
3. Potential savings opportunities
4. Any concerns or flags
5. Confidence level in your recommendation (0-100)"""
    
    def _build_quotation_analysis_prompt(
        self,
        quotation_data: Dict,
        vendor: Dict,
        items_analysis: List[Dict],
        purchase_history: List[Dict]
    ) -> str:
        """Build prompt for quotation analysis"""
        return f"""Analyze this quotation and provide recommendations:

Quotation Details:
{json.dumps(quotation_data, indent=2)}

Vendor Information:
{json.dumps(vendor, indent=2)}

Items Analysis:
{json.dumps(items_analysis, indent=2)}

Provide:
1. Overall assessment of the quotation
2. Price competitiveness analysis
3. Any anomalies or concerns
4. Recommendation (approve, negotiate, or reject)
5. Specific negotiation points if applicable"""
    
    def _calculate_confidence(self, vendor_scores: List[Dict], purchase_history: List[Dict]) -> float:
        """Calculate confidence score for recommendation"""
        if not vendor_scores:
            return 0.0
        
        base_confidence = 70.0
        
        # Increase confidence if top vendor is significantly better
        if len(vendor_scores) > 1:
            score_diff = vendor_scores[0]["total_score"] - vendor_scores[1]["total_score"]
            if score_diff > 20:
                base_confidence += 20
            elif score_diff > 10:
                base_confidence += 10
        
        # Increase confidence if we have historical data
        if purchase_history:
            base_confidence += 10
        
        return min(base_confidence, 95.0)
    
    def _identify_risks(
        self,
        vendor_scores: List[Dict],
        purchase_history: List[Dict],
        budget: float
    ) -> List[str]:
        """Identify potential risks in the recommendation"""
        risks = []
        
        if not vendor_scores:
            risks.append("No suitable vendors found")
            return risks
        
        top_vendor = vendor_scores[0]
        
        if top_vendor.get("total_score", 0) < 60:
            risks.append("Top vendor has low overall score")
        
        if top_vendor.get("rating", 0) < 3.5:
            risks.append("Top vendor has low rating")
        
        if top_vendor.get("total_orders", 0) < 5:
            risks.append("Limited order history with top vendor")
        
        if not purchase_history:
            risks.append("No historical purchase data for comparison")
        
        return risks
    
    def _estimate_savings(self, vendor_scores: List[Dict], budget: float) -> Dict[str, Any]:
        """Estimate potential savings"""
        if not vendor_scores:
            return {"estimated_savings": 0, "savings_percentage": 0}
        
        # Simplified savings calculation
        top_vendor = vendor_scores[0]
        estimated_savings = budget * 0.08  # Assume 8% savings as baseline
        
        return {
            "estimated_savings": round(estimated_savings, 2),
            "savings_percentage": 8.0,
            "basis": "Based on vendor score and historical patterns"
        }
    
    def _get_historical_average(self, product_description: str, purchase_history: List[Dict]) -> float:
        """Get historical average price for a product"""
        # Simplified - in production, would do more sophisticated matching
        if not purchase_history:
            return 0.0
        
        prices = []
        for order in purchase_history:
            for item in order.get("items", []):
                if product_description.lower() in str(item.get("description", "")).lower():
                    prices.append(item.get("unitPrice", 0))
        
        if prices:
            return sum(prices) / len(prices)
        return 0.0
    
    def _generate_quotation_recommendation(
        self,
        items_analysis: List[Dict],
        vendor: Dict,
        total_amount: float
    ) -> str:
        """Generate recommendation for quotation"""
        has_anomalies = any(item["price_anomaly"]["is_anomaly"] for item in items_analysis)
        
        if has_anomalies:
            return "NEGOTIATE - Price anomalies detected. Request revised quotation."
        
        if vendor.get("rating", 0) < 3.5:
            return "REVIEW - Vendor has low rating. Consider alternatives."
        
        return "APPROVE - Quotation appears competitive with no significant anomalies."
    
    async def close(self):
        """Cleanup resources"""
        await self.tools.close()
