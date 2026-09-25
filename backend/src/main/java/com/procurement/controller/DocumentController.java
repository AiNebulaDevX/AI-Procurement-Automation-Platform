package com.procurement.controller;

import com.procurement.entity.Document;
import com.procurement.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://frontend:3000"})
public class DocumentController {
    
    private final DocumentService documentService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'FINANCE_APPROVER', 'VIEWER')")
    public ResponseEntity<List<Document>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }
    
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'FINANCE_APPROVER', 'VIEWER')")
    public ResponseEntity<List<Document>> getDocumentsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(documentService.getDocumentsByUser(userId));
    }
    
    @GetMapping("/entity/{entityType}/{entityId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'FINANCE_APPROVER', 'VIEWER')")
    public ResponseEntity<List<Document>> getDocumentsByEntity(@PathVariable String entityType, @PathVariable Integer entityId) {
        return ResponseEntity.ok(documentService.getDocumentsByEntity(entityType, entityId));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'FINANCE_APPROVER', 'VIEWER')")
    public ResponseEntity<Document> getDocumentById(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }
    
    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'VIEWER')")
    public ResponseEntity<Document> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "entityType", required = false) String entityType,
            @RequestParam(value = "entityId", required = false) Integer entityId) throws IOException {
        return ResponseEntity.ok(documentService.uploadDocument(file, userId, entityType, entityId));
    }
    
    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'FINANCE_APPROVER', 'VIEWER')")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) throws IOException {
        Document document = documentService.getDocumentById(id);
        byte[] fileContent = documentService.downloadDocument(id);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getFileName() + "\"")
                .body(fileContent);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER')")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) throws IOException {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
