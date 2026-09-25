package com.procurement.controller;

import com.procurement.entity.Vendor;
import com.procurement.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://frontend:3000"})
public class VendorController {
    
    private final VendorService vendorService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'FINANCE_APPROVER', 'VIEWER')")
    public ResponseEntity<List<Vendor>> getAllVendors() {
        return ResponseEntity.ok(vendorService.getAllVendors());
    }
    
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'FINANCE_APPROVER', 'VIEWER')")
    public ResponseEntity<List<Vendor>> getActiveVendors() {
        return ResponseEntity.ok(vendorService.getActiveVendors());
    }
    
    @GetMapping("/top-rated")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'FINANCE_APPROVER', 'VIEWER')")
    public ResponseEntity<List<Vendor>> getTopVendorsByRating() {
        return ResponseEntity.ok(vendorService.getTopVendorsByRating());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'FINANCE_APPROVER', 'VIEWER')")
    public ResponseEntity<Vendor> getVendorById(@PathVariable Long id) {
        return ResponseEntity.ok(vendorService.getVendorById(id));
    }
    
    @GetMapping("/code/{code}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'FINANCE_APPROVER', 'VIEWER')")
    public ResponseEntity<Vendor> getVendorByCode(@PathVariable String code) {
        return ResponseEntity.ok(vendorService.getVendorByCode(code));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER')")
    public ResponseEntity<Vendor> createVendor(@RequestBody Vendor vendor) {
        return ResponseEntity.ok(vendorService.createVendor(vendor));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER')")
    public ResponseEntity<Vendor> updateVendor(@PathVariable Long id, @RequestBody Vendor vendor) {
        return ResponseEntity.ok(vendorService.updateVendor(id, vendor));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVendor(@PathVariable Long id) {
        vendorService.deleteVendor(id);
        return ResponseEntity.noContent().build();
    }
}
