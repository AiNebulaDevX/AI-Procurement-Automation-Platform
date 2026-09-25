package com.procurement.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuotationUploadedEvent {
    private String eventId;
    private Long quotationId;
    private String quotationNumber;
    private Long vendorId;
    private Long uploadedBy;
    private Double totalAmount;
    private LocalDateTime timestamp;
    private String documentPath;
}
