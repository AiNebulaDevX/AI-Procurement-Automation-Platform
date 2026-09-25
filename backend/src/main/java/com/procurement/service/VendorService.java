package com.procurement.service;

import com.procurement.entity.Vendor;
import com.procurement.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VendorService {
    
    private final VendorRepository vendorRepository;
    
    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }
    
    public List<Vendor> getActiveVendors() {
        return vendorRepository.findByIsActiveTrue();
    }
    
    public List<Vendor> getTopVendorsByRating() {
        return vendorRepository.findActiveVendorsByRatingDesc();
    }
    
    public Vendor getVendorById(Long id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
    }
    
    public Vendor getVendorByCode(String code) {
        return vendorRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
    }
    
    @Transactional
    public Vendor createVendor(Vendor vendor) {
        if (vendorRepository.existsByCode(vendor.getCode())) {
            throw new RuntimeException("Vendor code already exists");
        }
        return vendorRepository.save(vendor);
    }
    
    @Transactional
    public Vendor updateVendor(Long id, Vendor vendorDetails) {
        Vendor vendor = getVendorById(id);
        
        vendor.setName(vendorDetails.getName());
        vendor.setContactPerson(vendorDetails.getContactPerson());
        vendor.setEmail(vendorDetails.getEmail());
        vendor.setPhone(vendorDetails.getPhone());
        vendor.setAddress(vendorDetails.getAddress());
        vendor.setIsActive(vendorDetails.getIsActive());
        
        return vendorRepository.save(vendor);
    }
    
    @Transactional
    public void deleteVendor(Long id) {
        Vendor vendor = getVendorById(id);
        vendor.setIsActive(false);
        vendorRepository.save(vendor);
    }
    
    @Transactional
    public void updateVendorStats(Long vendorId, Integer orderCount, java.math.BigDecimal amount) {
        Vendor vendor = getVendorById(vendorId);
        vendor.setTotalOrders(vendor.getTotalOrders() + orderCount);
        vendor.setTotalAmount(vendor.getTotalAmount().add(amount));
        vendorRepository.save(vendor);
    }
}
