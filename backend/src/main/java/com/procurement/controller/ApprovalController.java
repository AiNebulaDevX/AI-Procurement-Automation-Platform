package com.procurement.controller;

import com.procurement.entity.Approval;
import com.procurement.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approvals")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://frontend:3000"})
public class ApprovalController {
    
    private final ApprovalService approvalService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'FINANCE_APPROVER', 'VIEWER')")
    public ResponseEntity<List<Approval>> getAllApprovals() {
        return ResponseEntity.ok(approvalService.getAllApprovals());
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'FINANCE_APPROVER', 'VIEWER')")
    public ResponseEntity<List<Approval>> getApprovalsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(approvalService.getApprovalsByStatus(Approval.ApprovalStatus.valueOf(status)));
    }
    
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'FINANCE_APPROVER', 'VIEWER')")
    public ResponseEntity<List<Approval>> getApprovalsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(approvalService.getApprovalsByUser(userId));
    }
    
    @GetMapping("/entity/{entityType}/{entityId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'FINANCE_APPROVER', 'VIEWER')")
    public ResponseEntity<Approval> getApprovalByEntity(@PathVariable String entityType, @PathVariable Integer entityId) {
        return ResponseEntity.ok(approvalService.getApprovalByEntity(entityType, entityId));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'FINANCE_APPROVER', 'VIEWER')")
    public ResponseEntity<Approval> getApprovalById(@PathVariable Long id) {
        return ResponseEntity.ok(approvalService.getApprovalById(id));
    }
    
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'FINANCE_APPROVER')")
    public ResponseEntity<Approval> approveApproval(
            @PathVariable Long id,
            @RequestParam(required = false) String comments,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long approverId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(approvalService.approveApproval(id, approverId, comments));
    }
    
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'FINANCE_APPROVER')")
    public ResponseEntity<Approval> rejectApproval(
            @PathVariable Long id,
            @RequestParam(required = false) String comments,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long approverId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(approvalService.rejectApproval(id, approverId, comments));
    }
    
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER')")
    public ResponseEntity<Approval> cancelApproval(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(approvalService.cancelApproval(id, userId));
    }
}
