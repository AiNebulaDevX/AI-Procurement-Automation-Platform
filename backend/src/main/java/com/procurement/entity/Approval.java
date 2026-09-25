package com.procurement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "approvals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Approval {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50)
    private String entityType;
    
    @Column(nullable = false)
    private Integer entityId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by")
    private User requestedBy;
    
    @Column(length = 50)
    private String currentApproverRole;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private ApprovalStatus status = ApprovalStatus.PENDING;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal amount;
    
    @Column(columnDefinition = "TEXT")
    private String comments;
    
    @OneToMany(mappedBy = "approval", cascade = CascadeType.ALL)
    private List<ApprovalHistory> history = new ArrayList<>();
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum ApprovalStatus {
        PENDING, APPROVED, REJECTED, CANCELLED
    }
}
