package com.procurement.repository;

import com.procurement.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByUploadedById(Long userId);
    List<Document> findByEntityTypeAndEntityId(String entityType, Integer entityId);
}
