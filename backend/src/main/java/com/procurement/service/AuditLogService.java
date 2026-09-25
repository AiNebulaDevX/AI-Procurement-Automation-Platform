package com.procurement.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.procurement.entity.AIDecisionLog;
import com.procurement.entity.User;
import com.procurement.repository.AIDecisionLogRepository;
import com.procurement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {
    
    private final AIDecisionLogRepository aiDecisionLogRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    
    @Transactional
    public AIDecisionLog logAIDecision(
            String taskType,
            Object inputData,
            Object retrievedDocuments,
            Object aiRecommendation,
            BigDecimal confidenceScore,
            Object toolsCalled,
            Long userId
    ) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            
            String requestId = UUID.randomUUID().toString();
            
            AIDecisionLog log = new AIDecisionLog();
            log.setRequestId(requestId);
            log.setUser(user);
            log.setTaskType(taskType);
            log.setInputData(objectMapper.writeValueAsString(inputData));
            log.setRetrievedDocuments(retrievedDocuments != null ? objectMapper.writeValueAsString(retrievedDocuments) : null);
            log.setAiRecommendation(objectMapper.writeValueAsString(aiRecommendation));
            log.setConfidenceScore(confidenceScore);
            log.setToolsCalled(toolsCalled != null ? objectMapper.writeValueAsString(toolsCalled) : null);
            log.setTimestamp(LocalDateTime.now());
            
            return aiDecisionLogRepository.save(log);
        } catch (Exception e) {
            log.error("Failed to log AI decision", e);
            throw new RuntimeException("Failed to log AI decision", e);
        }
    }
    
    @Transactional
    public AIDecisionLog updateHumanDecision(
            String requestId,
            String humanDecision,
            String humanComments
    ) {
        AIDecisionLog log = aiDecisionLogRepository.findByRequestId(requestId)
                .orElseThrow(() -> new RuntimeException("AI decision log not found"));
        
        log.setHumanDecision(humanDecision);
        log.setHumanComments(humanComments);
        
        return aiDecisionLogRepository.save(log);
    }
    
    public java.util.List<AIDecisionLog> getAIDecisionsByUser(Long userId) {
        return aiDecisionLogRepository.findByUserId(userId);
    }
    
    public java.util.List<AIDecisionLog> getAIDecisionsByTaskType(String taskType) {
        return aiDecisionLogRepository.findByTaskType(taskType);
    }
    
    public AIDecisionLog getAIDecisionByRequestId(String requestId) {
        return aiDecisionLogRepository.findByRequestId(requestId)
                .orElseThrow(() -> new RuntimeException("AI decision log not found"));
    }
}
