package com.procurement.repository;

import com.procurement.entity.Quotation;
import com.procurement.entity.Quotation.QuotationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, Long> {
    List<Quotation> findByVendorId(Long vendorId);
    List<Quotation> findByStatus(QuotationStatus status);
    List<Quotation> findByQuotationDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT q FROM Quotation q WHERE q.validUntil < :currentDate AND q.status = 'PENDING'")
    List<Quotation> findExpiredQuotations(LocalDate currentDate);
}
