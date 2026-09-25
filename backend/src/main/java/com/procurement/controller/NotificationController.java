package com.procurement.controller;

import com.procurement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://frontend:3000"})
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @PostMapping("/approval")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER')")
    public ResponseEntity<String> sendApprovalNotification(
            @RequestParam String toEmail,
            @RequestParam String approverName,
            @RequestParam String entityType,
            @RequestParam Integer entityId,
            @RequestParam String amount) {
        notificationService.sendApprovalNotification(toEmail, approverName, entityType, entityId, amount);
        return ResponseEntity.ok("Notification sent successfully");
    }
    
    @PostMapping("/decision")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER')")
    public ResponseEntity<String> sendApprovalDecisionNotification(
            @RequestParam String toEmail,
            @RequestParam String decision,
            @RequestParam String entityType,
            @RequestParam Integer entityId) {
        notificationService.sendApprovalDecisionNotification(toEmail, decision, entityType, entityId);
        return ResponseEntity.ok("Notification sent successfully");
    }
    
    @PostMapping("/analysis")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER')")
    public ResponseEntity<String> sendQuotationAnalysisNotification(
            @RequestParam String toEmail,
            @RequestParam String quotationNumber,
            @RequestParam String analysisResult) {
        notificationService.sendQuotationAnalysisNotification(toEmail, quotationNumber, analysisResult);
        return ResponseEntity.ok("Notification sent successfully");
    }
}
