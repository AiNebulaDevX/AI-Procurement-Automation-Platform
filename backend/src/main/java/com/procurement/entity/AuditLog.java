package com.procurement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(nullable = false, length = 100)
    private String action;
    
    @Column(length = 50)
    private String entityType;
    
    @Column
    private Integer entityId;
    
    @JdbcTypeCode(SqlTypes.JSON)
    private String oldValues;
    
    @JdbcTypeCode(SqlTypes.JSON)
    private String newValues;
    
    @Column(length = 45)
    private String ipAddress;
    
    @Column(columnDefinition = "TEXT")
    private String userAgent;
    
    @CreationTimestamp
    @Column(name = "timestamp", updatable = false)
    private LocalDateTime timestamp;
    
    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
