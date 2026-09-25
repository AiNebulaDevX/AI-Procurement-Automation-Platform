package com.procurement.service;

import com.procurement.entity.Vendor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnBean(CacheService.class)
public class CachedVendorService {
    
    private final VendorService vendorService;
    private final CacheService cacheService;
    
    private static final String VENDOR_CACHE_PREFIX = "vendor:";
    private static final String VENDORS_LIST_CACHE = "vendors:list";
    
    public List<Vendor> getAllVendors() {
        String cacheKey = VENDORS_LIST_CACHE;
        
        @SuppressWarnings("unchecked")
        List<Vendor> cached = (List<Vendor>) cacheService.get(cacheKey);
        if (cached != null) {
            log.debug("Returning cached vendors list");
            return cached;
        }
        
        List<Vendor> vendors = vendorService.getAllVendors();
        cacheService.set(cacheKey, vendors, Duration.ofMinutes(10));
        return vendors;
    }
    
    public Vendor getVendorById(Long id) {
        String cacheKey = VENDOR_CACHE_PREFIX + id;
        
        Vendor cached = (Vendor) cacheService.get(cacheKey);
        if (cached != null) {
            log.debug("Returning cached vendor: {}", id);
            return cached;
        }
        
        Vendor vendor = vendorService.getVendorById(id);
        cacheService.set(cacheKey, vendor, Duration.ofMinutes(30));
        return vendor;
    }
    
    public Vendor createVendor(Vendor vendor) {
        Vendor created = vendorService.createVendor(vendor);
        
        // Invalidate caches
        cacheService.delete(VENDORS_LIST_CACHE);
        cacheService.deletePattern(VENDOR_CACHE_PREFIX + "*");
        
        return created;
    }
    
    public Vendor updateVendor(Long id, Vendor vendorDetails) {
        Vendor updated = vendorService.updateVendor(id, vendorDetails);
        
        // Invalidate specific cache
        cacheService.delete(VENDOR_CACHE_PREFIX + id);
        cacheService.delete(VENDORS_LIST_CACHE);
        
        return updated;
    }
    
    public void deleteVendor(Long id) {
        vendorService.deleteVendor(id);
        
        // Invalidate caches
        cacheService.delete(VENDOR_CACHE_PREFIX + id);
        cacheService.delete(VENDORS_LIST_CACHE);
    }
}
