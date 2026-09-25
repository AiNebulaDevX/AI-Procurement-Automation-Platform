package com.procurement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contracts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contract {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;
    
    @Column(unique = true, nullable = false, length = 50)
    private String contractNumber;
    
    @Column(length = 50)
    private String contractType;
    
    @Column(nullable = false)
    private LocalDate startDate;
    
    @Column
    private LocalDate endDate;
    
    @Column(columnDefinition = "TEXT")
    private String terms;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal totalValue;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private ContractStatus status = ContractStatus.ACTIVE;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum ContractStatus {
        ACTIVE, EXPIRED, TERMINATED
    }
}
