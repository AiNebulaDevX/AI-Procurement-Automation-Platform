package com.procurement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIService {
    
    @Value("${ai.service.url}")
    private String aiServiceUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    public void analyzeQuotation(Long quotationId) {
        try {
            String url = aiServiceUrl + "/agent/analyze";
            
            Map<String, Object> request = new HashMap<>();
            request.put("quotationId", quotationId);
            request.put("taskType", "QUOTATION_ANALYSIS");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            
            restTemplate.postForObject(url, entity, String.class);
            
            log.info("AI analysis triggered for quotation: {}", quotationId);
        } catch (Exception e) {
            log.error("Failed to trigger AI analysis for quotation: {}", quotationId, e);
        }
    }
    
    public Map<String, Object> getVendorRecommendation(Map<String, Object> criteria) {
        try {
            String url = aiServiceUrl + "/agent/recommend";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(criteria, headers);
            
            return restTemplate.postForObject(url, entity, Map.class);
        } catch (Exception e) {
            log.error("Failed to get vendor recommendation", e);
            return Map.of("error", "Failed to get recommendation");
        }
    }
    
    public String generateNegotiationEmail(Map<String, Object> context) {
        try {
            String url = aiServiceUrl + "/agent/generate-negotiation-email";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(context, headers);
            
            return restTemplate.postForObject(url, entity, String.class);
        } catch (Exception e) {
            log.error("Failed to generate negotiation email", e);
            return "Failed to generate email";
        }
    }
}
