package com.procurement.controller;

import com.procurement.entity.PurchaseOrder;
import com.procurement.entity.Quotation;
import com.procurement.service.ProcurementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/procurement")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://frontend:3000"})
public class ProcurementController {
    
    private final ProcurementService procurementService;
    
    // Quotation endpoints
    @GetMapping("/quotations")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'FINANCE_APPROVER', 'VIEWER')")
    public ResponseEntity<List<Quotation>> getAllQuotations() {
        return ResponseEntity.ok(procurementService.getAllQuotations());
    }
    
    @GetMapping("/quotations/vendor/{vendorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'FINANCE_APPROVER', 'VIEWER')")
    public ResponseEntity<List<Quotation>> getQuotationsByVendor(@PathVariable Long vendorId) {
        return ResponseEntity.ok(procurementService.getQuotationsByVendor(vendorId));
    }
    
    @GetMapping("/quotations/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'FINANCE_APPROVER', 'VIEWER')")
    public ResponseEntity<List<Quotation>> getQuotationsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(procurementService.getQuotationsByStatus(Quotation.QuotationStatus.valueOf(status)));
    }
    
    @GetMapping("/quotations/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'FINANCE_APPROVER', 'VIEWER')")
    public ResponseEntity<Quotation> getQuotationById(@PathVariable Long id) {
        return ResponseEntity.ok(procurementService.getQuotationById(id));
    }
    
    @PostMapping("/quotations")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER')")
    public ResponseEntity<Quotation> createQuotation(@RequestBody Quotation quotation) {
        return ResponseEntity.ok(procurementService.createQuotation(quotation));
    }
    
    @PutMapping("/quotations/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER')")
    public ResponseEntity<Quotation> updateQuotationStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(procurementService.updateQuotationStatus(id, Quotation.QuotationStatus.valueOf(status)));
    }
    
    // Purchase Order endpoints
    @GetMapping("/purchase-orders")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'FINANCE_APPROVER', 'VIEWER')")
    public ResponseEntity<List<PurchaseOrder>> getAllPurchaseOrders() {
        return ResponseEntity.ok(procurementService.getAllPurchaseOrders());
    }
    
    @GetMapping("/purchase-orders/vendor/{vendorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'FINANCE_APPROVER', 'VIEWER')")
    public ResponseEntity<List<PurchaseOrder>> getPurchaseOrdersByVendor(@PathVariable Long vendorId) {
        return ResponseEntity.ok(procurementService.getPurchaseOrdersByVendor(vendorId));
    }
    
    @GetMapping("/purchase-orders/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'FINANCE_APPROVER', 'VIEWER')")
    public ResponseEntity<PurchaseOrder> getPurchaseOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(procurementService.getPurchaseOrderById(id));
    }
    
    @PostMapping("/purchase-orders")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER')")
    public ResponseEntity<PurchaseOrder> createPurchaseOrder(@RequestBody PurchaseOrder purchaseOrder) {
        return ResponseEntity.ok(procurementService.createPurchaseOrder(purchaseOrder));
    }
    
    @PostMapping("/purchase-orders/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE_APPROVER')")
    public ResponseEntity<PurchaseOrder> approvePurchaseOrder(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(procurementService.approvePurchaseOrder(id, Long.valueOf(userDetails.getUsername())));
    }
    
    @PostMapping("/purchase-orders/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE_APPROVER')")
    public ResponseEntity<PurchaseOrder> rejectPurchaseOrder(@PathVariable Long id) {
        return ResponseEntity.ok(procurementService.rejectPurchaseOrder(id));
    }
}
