package com.procurement.controller;

import com.procurement.entity.AIDecisionLog;
import com.procurement.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://frontend:3000"})
public class AuditLogController {
    
    private final AuditLogService auditLogService;
    
    @PostMapping("/ai-decision")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'VIEWER')")
    public ResponseEntity<AIDecisionLog> logAIDecision(@RequestBody Map<String, Object> request) {
        String taskType = (String) request.get("taskType");
        Object inputData = request.get("inputData");
        Object retrievedDocuments = request.get("retrievedDocuments");
        Object aiRecommendation = request.get("aiRecommendation");
        Double confidenceScore = request.get("confidenceScore") != null ? 
            ((Number) request.get("confidenceScore")).doubleValue() : null;
        Object toolsCalled = request.get("toolsCalled");
        Long userId = request.get("userId") != null ? 
            ((Number) request.get("userId")).longValue() : null;
        
        AIDecisionLog log = auditLogService.logAIDecision(
            taskType,
            inputData,
            retrievedDocuments,
            aiRecommendation,
            confidenceScore != null ? java.math.BigDecimal.valueOf(confidenceScore) : null,
            toolsCalled,
            userId
        );
        
        return ResponseEntity.ok(log);
    }
    
    @PutMapping("/ai-decision/{requestId}/human-decision")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'VIEWER')")
    public ResponseEntity<AIDecisionLog> updateHumanDecision(
            @PathVariable String requestId,
            @RequestBody Map<String, String> request) {
        String humanDecision = request.get("humanDecision");
        String humanComments = request.get("humanComments");
        
        AIDecisionLog log = auditLogService.updateHumanDecision(requestId, humanDecision, humanComments);
        return ResponseEntity.ok(log);
    }
    
    @GetMapping("/ai-decisions/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER')")
    public ResponseEntity<List<AIDecisionLog>> getAIDecisionsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(auditLogService.getAIDecisionsByUser(userId));
    }
    
    @GetMapping("/ai-decisions/task-type/{taskType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER')")
    public ResponseEntity<List<AIDecisionLog>> getAIDecisionsByTaskType(@PathVariable String taskType) {
        return ResponseEntity.ok(auditLogService.getAIDecisionsByTaskType(taskType));
    }
    
    @GetMapping("/ai-decisions/{requestId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'VIEWER')")
    public ResponseEntity<AIDecisionLog> getAIDecisionByRequestId(@PathVariable String requestId) {
        return ResponseEntity.ok(auditLogService.getAIDecisionByRequestId(requestId));
    }
}
