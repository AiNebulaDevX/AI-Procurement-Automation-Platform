package com.procurement.service;

import com.procurement.entity.Document;
import com.procurement.entity.User;
import com.procurement.repository.DocumentRepository;
import com.procurement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {
    
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;
    
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }
    
    public List<Document> getDocumentsByUser(Long userId) {
        return documentRepository.findByUploadedById(userId);
    }
    
    public List<Document> getDocumentsByEntity(String entityType, Integer entityId) {
        return documentRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }
    
    public Document getDocumentById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }
    
    public Document uploadDocument(MultipartFile file, Long userId, String entityType, Integer entityId) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        String filePath = uploadPath.resolve(uniqueFilename).toString();
        
        // Save file
        Files.copy(file.getInputStream(), Paths.get(filePath));
        
        Document document = Document.builder()
                .fileName(originalFilename)
                .filePath(filePath)
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .uploadedBy(user)
                .entityType(entityType)
                .entityId(entityId)
                .build();
        
        return documentRepository.save(document);
    }
    
    public byte[] downloadDocument(Long id) throws IOException {
        Document document = getDocumentById(id);
        Path filePath = Paths.get(document.getFilePath());
        return Files.readAllBytes(filePath);
    }
    
    public void deleteDocument(Long id) throws IOException {
        Document document = getDocumentById(id);
        Path filePath = Paths.get(document.getFilePath());
        Files.deleteIfExists(filePath);
        documentRepository.deleteById(id);
    }
}
