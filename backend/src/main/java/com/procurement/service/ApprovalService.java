package com.procurement.service;

import com.procurement.entity.*;
import com.procurement.repository.ApprovalRepository;
import com.procurement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApprovalService {
    
    private final ApprovalRepository approvalRepository;
    private final UserRepository userRepository;
    
    public List<Approval> getAllApprovals() {
        return approvalRepository.findAll();
    }
    
    public List<Approval> getApprovalsByStatus(Approval.ApprovalStatus status) {
        return approvalRepository.findByStatus(status);
    }
    
    public List<Approval> getApprovalsByUser(Long userId) {
        return approvalRepository.findByRequestedById(userId);
    }
    
    public Approval getApprovalById(Long id) {
        return approvalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Approval not found"));
    }
    
    public Approval getApprovalByEntity(String entityType, Integer entityId) {
        return approvalRepository.findByEntityTypeAndEntityId(entityType, entityId)
                .orElseThrow(() -> new RuntimeException("Approval not found"));
    }
    
    @Transactional
    public Approval createApproval(String entityType, Integer entityId, BigDecimal amount, Long requestedById) {
        User requestedBy = userRepository.findById(requestedById)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String currentApproverRole = determineApproverRole(amount);
        
        Approval approval = Approval.builder()
                .entityType(entityType)
                .entityId(entityId)
                .requestedBy(requestedBy)
                .currentApproverRole(currentApproverRole)
                .status(Approval.ApprovalStatus.PENDING)
                .amount(amount)
                .build();
        
        return approvalRepository.save(approval);
    }
    
    @Transactional
    public Approval approveApproval(Long id, Long approverId, String comments) {
        Approval approval = getApprovalById(id);
        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new RuntimeException("Approver not found"));
        
        // Check if approver has the right role
        if (!approver.getRole().name().equals(approval.getCurrentApproverRole()) && 
            !approver.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("User does not have permission to approve this request");
        }
        
        // Add approval history
        ApprovalHistory history = ApprovalHistory.builder()
                .approval(approval)
                .approver(approver)
                .action(ApprovalHistory.ApprovalAction.APPROVED)
                .comments(comments)
                .build();
        approval.getHistory().add(history);
        
        // Check if more approvals are needed
        if (needsFurtherApproval(approval.getAmount(), approval.getCurrentApproverRole())) {
            approval.setCurrentApproverRole(getNextApproverRole(approval.getCurrentApproverRole()));
        } else {
            approval.setStatus(Approval.ApprovalStatus.APPROVED);
        }
        
        return approvalRepository.save(approval);
    }
    
    @Transactional
    public Approval rejectApproval(Long id, Long approverId, String comments) {
        Approval approval = getApprovalById(id);
        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new RuntimeException("Approver not found"));
        
        // Check if approver has the right role
        if (!approver.getRole().name().equals(approval.getCurrentApproverRole()) && 
            !approver.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("User does not have permission to reject this request");
        }
        
        // Add rejection history
        ApprovalHistory history = ApprovalHistory.builder()
                .approval(approval)
                .approver(approver)
                .action(ApprovalHistory.ApprovalAction.REJECTED)
                .comments(comments)
                .build();
        approval.getHistory().add(history);
        
        approval.setStatus(Approval.ApprovalStatus.REJECTED);
        approval.setComments(comments);
        
        return approvalRepository.save(approval);
    }
    
    @Transactional
    public Approval cancelApproval(Long id, Long userId) {
        Approval approval = getApprovalById(id);
        
        if (!approval.getRequestedBy().getId().equals(userId) && 
            !userRepository.findById(userId).get().getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("User does not have permission to cancel this request");
        }
        
        approval.setStatus(Approval.ApprovalStatus.CANCELLED);
        return approvalRepository.save(approval);
    }
    
    private String determineApproverRole(BigDecimal amount) {
        if (amount.compareTo(new BigDecimal("50000")) < 0) {
            return "PROCUREMENT_MANAGER";
        } else if (amount.compareTo(new BigDecimal("500000")) < 0) {
            return "FINANCE_APPROVER";
        } else {
            return "ADMIN";
        }
    }
    
    private boolean needsFurtherApproval(BigDecimal amount, String currentRole) {
        if (currentRole.equals("PROCUREMENT_MANAGER") && amount.compareTo(new BigDecimal("50000")) >= 0) {
            return true;
        }
        if (currentRole.equals("FINANCE_APPROVER") && amount.compareTo(new BigDecimal("500000")) >= 0) {
            return true;
        }
        return false;
    }
    
    private String getNextApproverRole(String currentRole) {
        return switch (currentRole) {
            case "PROCUREMENT_MANAGER" -> "FINANCE_APPROVER";
            case "FINANCE_APPROVER" -> "ADMIN";
            default -> currentRole;
        };
    }
}
