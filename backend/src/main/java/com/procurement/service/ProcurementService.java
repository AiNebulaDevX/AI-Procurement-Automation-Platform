package com.procurement.service;

import com.procurement.entity.*;
import com.procurement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcurementService {
    
    private final QuotationRepository quotationRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final VendorRepository vendorRepository;
    private final ProductRepository productRepository;
    private final ApprovalService approvalService;
    
    // Quotation operations
    public List<Quotation> getAllQuotations() {
        return quotationRepository.findAll();
    }
    
    public List<Quotation> getQuotationsByVendor(Long vendorId) {
        return quotationRepository.findByVendorId(vendorId);
    }
    
    public List<Quotation> getQuotationsByStatus(Quotation.QuotationStatus status) {
        return quotationRepository.findByStatus(status);
    }
    
    public Quotation getQuotationById(Long id) {
        return quotationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quotation not found"));
    }
    
    @Transactional
    public Quotation createQuotation(Quotation quotation) {
        quotation.setQuotationDate(LocalDate.now());
        quotation.setStatus(Quotation.QuotationStatus.PENDING);
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (QuotationItem item : quotation.getItems()) {
            item.setTotalPrice(item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())));
            totalAmount = totalAmount.add(item.getTotalPrice());
            item.setQuotation(quotation);
        }
        quotation.setTotalAmount(totalAmount);
        
        return quotationRepository.save(quotation);
    }
    
    @Transactional
    public Quotation updateQuotationStatus(Long id, Quotation.QuotationStatus status) {
        Quotation quotation = getQuotationById(id);
        quotation.setStatus(status);
        return quotationRepository.save(quotation);
    }
    
    // Purchase Order operations
    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }
    
    public List<PurchaseOrder> getPurchaseOrdersByVendor(Long vendorId) {
        return purchaseOrderRepository.findByVendorId(vendorId);
    }
    
    public List<PurchaseOrder> getPurchaseOrdersByStatus(PurchaseOrder.PurchaseOrderStatus status) {
        return purchaseOrderRepository.findByStatus(status);
    }
    
    public PurchaseOrder getPurchaseOrderById(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found"));
    }
    
    @Transactional
    public PurchaseOrder createPurchaseOrder(PurchaseOrder purchaseOrder) {
        purchaseOrder.setPoNumber(generatePONumber());
        purchaseOrder.setStatus(PurchaseOrder.PurchaseOrderStatus.PENDING);
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (PurchaseOrderItem item : purchaseOrder.getItems()) {
            item.setTotalPrice(item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())));
            totalAmount = totalAmount.add(item.getTotalPrice());
            item.setPurchaseOrder(purchaseOrder);
        }
        purchaseOrder.setTotalAmount(totalAmount);
        
        PurchaseOrder savedPO = purchaseOrderRepository.save(purchaseOrder);
        
        // Trigger approval workflow
        approvalService.createApproval("PURCHASE_ORDER", savedPO.getId().intValue(), 
                savedPO.getTotalAmount(), savedPO.getRequestedBy().getId());
        
        return savedPO;
    }
    
    @Transactional
    public PurchaseOrder approvePurchaseOrder(Long id, Long approverId) {
        PurchaseOrder purchaseOrder = getPurchaseOrderById(id);
        purchaseOrder.setStatus(PurchaseOrder.PurchaseOrderStatus.APPROVED);
        purchaseOrder.setApprovedAt(java.time.LocalDateTime.now());
        
        // Update vendor stats
        if (purchaseOrder.getVendor() != null) {
            vendorService.updateVendorStats(purchaseOrder.getVendor().getId(), 1, purchaseOrder.getTotalAmount());
        }
        
        return purchaseOrderRepository.save(purchaseOrder);
    }
    
    @Transactional
    public PurchaseOrder rejectPurchaseOrder(Long id) {
        PurchaseOrder purchaseOrder = getPurchaseOrderById(id);
        purchaseOrder.setStatus(PurchaseOrder.PurchaseOrderStatus.REJECTED);
        return purchaseOrderRepository.save(purchaseOrder);
    }
    
    private String generatePONumber() {
        return "PO-" + System.currentTimeMillis();
    }
    
    private final VendorService vendorService;
}
