package com.procurement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "quotation_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuotationItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotation_id")
    private Quotation quotation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalPrice;
    
    @Column(columnDefinition = "TEXT")
    private String warranty;
    
    @Column(length = 100)
    private String deliveryTimeline;
    
    @JdbcTypeCode(SqlTypes.JSON)
    private String specifications;
}
