package com.procurement.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalCompletedEvent {
    private String eventId;
    private Long approvalId;
    private String entityType;
    private Integer entityId;
    private String status;
    private Long approverId;
    private String comments;
    private LocalDateTime timestamp;
}
