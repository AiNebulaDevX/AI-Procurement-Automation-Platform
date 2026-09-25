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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quotations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quotation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;
    
    @Column(unique = true, nullable = false, length = 50)
    private String quotationNumber;
    
    @Column(nullable = false)
    private LocalDate quotationDate;
    
    @Column
    private LocalDate validUntil;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(length = 10)
    private String currency = "USD";
    
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private QuotationStatus status = QuotationStatus.PENDING;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;
    
    @OneToMany(mappedBy = "quotation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuotationItem> items = new ArrayList<>();
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum QuotationStatus {
        PENDING, UNDER_REVIEW, APPROVED, REJECTED, EXPIRED
    }
}
