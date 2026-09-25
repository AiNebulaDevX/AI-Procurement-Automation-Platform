package com.procurement.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.procurement.kafka.events.ApprovalCompletedEvent;
import com.procurement.kafka.events.PurchaseOrderCreatedEvent;
import com.procurement.kafka.events.QuotationUploadedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers")
public class KafkaProducerService {
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    public void sendQuotationUploadedEvent(QuotationUploadedEvent event) {
        try {
            event.setEventId(UUID.randomUUID().toString());
            event.setTimestamp(java.time.LocalDateTime.now());
            
            String jsonEvent = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("quotation-uploaded", event.getQuotationId().toString(), jsonEvent);
            
            log.info("QuotationUploadedEvent sent: {}", event.getQuotationId());
        } catch (Exception e) {
            log.error("Failed to send QuotationUploadedEvent", e);
        }
    }
    
    public void sendPurchaseOrderCreatedEvent(PurchaseOrderCreatedEvent event) {
        try {
            event.setEventId(UUID.randomUUID().toString());
            event.setTimestamp(java.time.LocalDateTime.now());
            
            String jsonEvent = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("purchase-order-created", event.getPurchaseOrderId().toString(), jsonEvent);
            
            log.info("PurchaseOrderCreatedEvent sent: {}", event.getPurchaseOrderId());
        } catch (Exception e) {
            log.error("Failed to send PurchaseOrderCreatedEvent", e);
        }
    }
    
    public void sendApprovalCompletedEvent(ApprovalCompletedEvent event) {
        try {
            event.setEventId(UUID.randomUUID().toString());
            event.setTimestamp(java.time.LocalDateTime.now());
            
            String jsonEvent = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("approval-completed", event.getApprovalId().toString(), jsonEvent);
            
            log.info("ApprovalCompletedEvent sent: {}", event.getApprovalId());
        } catch (Exception e) {
            log.error("Failed to send ApprovalCompletedEvent", e);
        }
    }
}
