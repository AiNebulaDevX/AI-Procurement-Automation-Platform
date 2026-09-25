package com.procurement.repository;

import com.procurement.entity.PurchaseOrder;
import com.procurement.entity.PurchaseOrder.PurchaseOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findByVendorId(Long vendorId);
    List<PurchaseOrder> findByStatus(PurchaseOrderStatus status);
    List<PurchaseOrder> findByRequestedById(Long userId);
}
