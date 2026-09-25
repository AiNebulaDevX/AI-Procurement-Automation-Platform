package com.procurement.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderCreatedEvent {
    private String eventId;
    private Long purchaseOrderId;
    private String poNumber;
    private Long vendorId;
    private Double totalAmount;
    private Long requestedBy;
    private LocalDateTime timestamp;
}
