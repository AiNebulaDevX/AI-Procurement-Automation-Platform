package com.procurement.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {
    
    public void sendApprovalNotification(String toEmail, String approverName, String entityType, Integer entityId, String amount) {
        try {
            log.info("[MOCK] Approval notification would be sent to {}: Approval Required - {} #{} Amount: {}", 
                toEmail, entityType, entityId, amount);
        } catch (Exception e) {
            log.error("Failed to send approval notification to {}", toEmail, e);
        }
    }
    
    public void sendApprovalDecisionNotification(String toEmail, String decision, String entityType, Integer entityId) {
        try {
            log.info("[MOCK] Approval decision notification would be sent to {}: {} - {} #{}", 
                toEmail, decision, entityType, entityId);
        } catch (Exception e) {
            log.error("Failed to send approval decision notification to {}", toEmail, e);
        }
    }
    
    public void sendQuotationAnalysisNotification(String toEmail, String quotationNumber, String analysisResult) {
        try {
            log.info("[MOCK] Quotation analysis notification would be sent to {}: AI Analysis Complete - Quotation {}", 
                toEmail, quotationNumber);
        } catch (Exception e) {
            log.error("Failed to send quotation analysis notification to {}", toEmail, e);
        }
    }
}
