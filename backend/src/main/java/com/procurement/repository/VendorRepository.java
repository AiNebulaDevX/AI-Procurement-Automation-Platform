package com.procurement.repository;

import com.procurement.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByCode(String code);
    boolean existsByCode(String code);
    List<Vendor> findByIsActiveTrue();
    List<Vendor> findByRatingGreaterThanEqual(Double rating);
    
    @Query("SELECT v FROM Vendor v WHERE v.isActive = true ORDER BY v.rating DESC")
    List<Vendor> findActiveVendorsByRatingDesc();
}
