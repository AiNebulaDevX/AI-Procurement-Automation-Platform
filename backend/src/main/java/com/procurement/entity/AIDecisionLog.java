package com.procurement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_decision_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIDecisionLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 100)
    private String requestId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(nullable = false, length = 50)
    private String taskType;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "JSONB")
    private String inputData;
    
    @JdbcTypeCode(SqlTypes.JSON)
    private String retrievedDocuments;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "JSONB")
    private String aiRecommendation;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal confidenceScore;
    
    @JdbcTypeCode(SqlTypes.JSON)
    private String toolsCalled;
    
    @Column(length = 50)
    private String humanDecision;
    
    @Column(columnDefinition = "TEXT")
    private String humanComments;
    
    @CreationTimestamp
    @Column(name = "timestamp", updatable = false)
    private LocalDateTime timestamp;
}
