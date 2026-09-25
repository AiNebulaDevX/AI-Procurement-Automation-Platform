package com.procurement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "approval_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_id")
    private Approval approval;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id")
    private User approver;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ApprovalAction action;
    
    @Column(columnDefinition = "TEXT")
    private String comments;
    
    @CreationTimestamp
    @Column(name = "timestamp", updatable = false)
    private LocalDateTime timestamp;
    
    public enum ApprovalAction {
        APPROVED, REJECTED, CANCELLED
    }
}
