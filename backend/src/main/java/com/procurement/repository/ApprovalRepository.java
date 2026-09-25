package com.procurement.repository;

import com.procurement.entity.Approval;
import com.procurement.entity.Approval.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {
    Optional<Approval> findByEntityTypeAndEntityId(String entityType, Integer entityId);
    List<Approval> findByStatus(ApprovalStatus status);
    List<Approval> findByRequestedById(Long userId);
}
