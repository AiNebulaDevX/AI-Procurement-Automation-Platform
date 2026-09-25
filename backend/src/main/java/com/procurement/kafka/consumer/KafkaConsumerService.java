package com.procurement.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.procurement.kafka.events.ApprovalCompletedEvent;
import com.procurement.kafka.events.PurchaseOrderCreatedEvent;
import com.procurement.kafka.events.QuotationUploadedEvent;
import com.procurement.service.AIService;
import com.procurement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers")
public class KafkaConsumerService {
    
    private final ObjectMapper objectMapper;
    private final AIService aiService;
    private final NotificationService notificationService;
    
    @KafkaListener(topics = "quotation-uploaded", groupId = "procurement-group")
    public void handleQuotationUploaded(String message) {
        try {
            QuotationUploadedEvent event = objectMapper.readValue(message, QuotationUploadedEvent.class);
            log.info("Received QuotationUploadedEvent: {}", event.getQuotationId());
            
            // Trigger AI analysis
            aiService.analyzeQuotation(event.getQuotationId());
            
        } catch (Exception e) {
            log.error("Failed to process QuotationUploadedEvent", e);
        }
    }
    
    @KafkaListener(topics = "purchase-order-created", groupId = "procurement-group")
    public void handlePurchaseOrderCreated(String message) {
        try {
            PurchaseOrderCreatedEvent event = objectMapper.readValue(message, PurchaseOrderCreatedEvent.class);
            log.info("Received PurchaseOrderCreatedEvent: {}", event.getPurchaseOrderId());
            
            // Send notification to approvers
            notificationService.sendApprovalNotification(
                "approver@procurement.com",
                "Approver",
                "PURCHASE_ORDER",
                event.getPurchaseOrderId().intValue(),
                event.getTotalAmount().toString()
            );
            
        } catch (Exception e) {
            log.error("Failed to process PurchaseOrderCreatedEvent", e);
        }
    }
    
    @KafkaListener(topics = "approval-completed", groupId = "procurement-group")
    public void handleApprovalCompleted(String message) {
        try {
            ApprovalCompletedEvent event = objectMapper.readValue(message, ApprovalCompletedEvent.class);
            log.info("Received ApprovalCompletedEvent: {}", event.getApprovalId());
            
            // Send notification to requester
            notificationService.sendApprovalDecisionNotification(
                "requester@procurement.com",
                event.getStatus(),
                event.getEntityType(),
                event.getEntityId()
            );
            
        } catch (Exception e) {
            log.error("Failed to process ApprovalCompletedEvent", e);
        }
    }
}
