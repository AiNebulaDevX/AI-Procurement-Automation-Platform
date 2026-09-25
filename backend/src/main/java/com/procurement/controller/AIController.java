package com.procurement.controller;

import com.procurement.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://frontend:3000"})
public class AIController {
    
    private final AIService aiService;
    
    @PostMapping("/recommend")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'VIEWER')")
    public ResponseEntity<Map<String, Object>> getVendorRecommendation(@RequestBody Map<String, Object> criteria) {
        return ResponseEntity.ok(aiService.getVendorRecommendation(criteria));
    }
    
    @PostMapping("/negotiation-email")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER')")
    public ResponseEntity<String> generateNegotiationEmail(@RequestBody Map<String, Object> context) {
        return ResponseEntity.ok(aiService.generateNegotiationEmail(context));
    }
}
