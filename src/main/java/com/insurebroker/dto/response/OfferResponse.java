package com.insurebroker.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class OfferResponse {
    private Long id;
    private String offerNumber;
    private Long brokerId;
    private String brokerName;
    private String brokerEmail;
    private String brokerLicense;
    private Long clientId;
    private String clientName;
    private Long productId;
    private String productName;
    private BigDecimal premium;
    private String status;
    private LocalDate expiresAt;
    private LocalDateTime createdAt;
    private String insurerName;
    private BigDecimal sumInsured;
    private Boolean gdprConsent;
    private LocalDate gdprConsentDate;
    private LocalDate startDate;
    private LocalDate endDate;
}