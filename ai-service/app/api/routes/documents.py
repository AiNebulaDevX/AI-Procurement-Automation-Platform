"""
API routes for document processing endpoints
"""

from fastapi import APIRouter, HTTPException, UploadFile, File
from typing import Dict, Any
import logging
import aiofiles
import os

router = APIRouter()
logger = logging.getLogger(__name__)

UPLOAD_DIR = "documents"


@router.post("/extract-pdf")
async def extract_pdf_text(file: UploadFile = File(...)):
    """
    Extract text from a PDF file
    
    This endpoint uses PDF parsing to extract structured information
    """
    try:
        # Ensure upload directory exists
        os.makedirs(UPLOAD_DIR, exist_ok=True)
        
        # Save uploaded file
        file_path = os.path.join(UPLOAD_DIR, file.filename)
        async with aiofiles.open(file_path, 'wb') as f:
            content = await file.read()
            await f.write(content)
        
        # Extract text using pdfplumber
        import pdfplumber
        
        extracted_text = ""
        with pdfplumber.open(file_path) as pdf:
            for page in pdf.pages:
                extracted_text += page.extract_text() + "\n"
        
        # Try to extract structured information
        structured_data = extract_quotation_data(extracted_text)
        
        return {
            "status": "success",
            "file_name": file.filename,
            "extracted_text": extracted_text[:5000],  # Limit text length
            "structured_data": structured_data
        }
    except Exception as e:
        logger.error(f"Error extracting PDF: {e}")
        raise HTTPException(status_code=500, detail=str(e))


def extract_quotation_data(text: str) -> Dict[str, Any]:
    """
    Extract structured quotation data from text
    This is a simplified extraction - production would use more sophisticated NLP
    """
    import re
    
    data = {
        "vendor": None,
        "items": [],
        "total_amount": None,
        "currency": "USD"
    }
    
    # Try to extract vendor name (simplified pattern)
    vendor_patterns = [
        r"Vendor:\s*(.+)",
        r"From:\s*(.+)",
        r"Supplier:\s*(.+)"
    ]
    
    for pattern in vendor_patterns:
        match = re.search(pattern, text, re.IGNORECASE)
        if match:
            data["vendor"] = match.group(1).strip()
            break
    
    # Try to extract total amount
    amount_patterns = [
        r"Total:\s*\$?([\d,]+\.?\d*)",
        r"Amount:\s*\$?([\d,]+\.?\d*)",
        r"Grand Total:\s*\$?([\d,]+\.?\d*)"
    ]
    
    for pattern in amount_patterns:
        match = re.search(pattern, text, re.IGNORECASE)
        if match:
            data["total_amount"] = float(match.group(1).replace(',', ''))
            break
    
    # Try to extract items (simplified)
    item_pattern = r"(\d+)\s+(.+?)\s+\$?([\d,]+\.?\d*)"
    items = re.findall(item_pattern, text)
    
    for item in items:
        data["items"].append({
            "quantity": int(item[0]),
            "description": item[1].strip(),
            "unit_price": float(item[2].replace(',', ''))
        })
    
    return data


@router.post("/parse-quotation")
async def parse_quotation(request: Dict[str, Any]):
    """
    Parse quotation data using AI for better extraction
    
    Request body:
    {
        "text": str,
        "file_type": str
    }
    """
    try:
        text = request.get("text")
        
        if not text:
            raise HTTPException(status_code=400, detail="text is required")
        
        # Use AI to parse the quotation
        # TODO: Implement AI-based parsing with LLM
        
        structured_data = extract_quotation_data(text)
        
        return {
            "status": "success",
            "structured_data": structured_data
        }
    except Exception as e:
        logger.error(f"Error parsing quotation: {e}")
        raise HTTPException(status_code=500, detail=str(e))
