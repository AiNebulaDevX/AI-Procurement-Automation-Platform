package com.procurement.repository;

import com.procurement.entity.AIDecisionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AIDecisionLogRepository extends JpaRepository<AIDecisionLog, Long> {
    Optional<AIDecisionLog> findByRequestId(String requestId);
    List<AIDecisionLog> findByUserId(Long userId);
    List<AIDecisionLog> findByTaskType(String taskType);
}
