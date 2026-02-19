package com.insurebroker.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class PolicyResponse {
    private Long id;
    private String policyNumber;
    private Long offerId;
    private Long brokerId;
    private String brokerName;
    private String brokerEmail;
    private String brokerLicense;
    private Long clientId;
    private String clientName;
    private Long productId;
    private String productName;
    private String insurerName;
    private BigDecimal premium;
    private BigDecimal sumInsured;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String paymentMethod;
    private String paymentStatus;
    private String cancellationReason;
    private String proofOfPayment;
    private Long validatedBy;
    private String validatedAt;
    private Boolean gdprConsent;
    private LocalDate gdprConsentDate;
    private LocalDateTime createdAt;
}