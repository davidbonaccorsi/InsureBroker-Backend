package com.insurebroker.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
public class OfferRequest {
    private Long clientId;
    private String clientName;
    private Long productId;
    private String productName;
    private String insurerName;
    private Long brokerId;
    private String brokerName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal sumInsured;
    private BigDecimal premium;
    private String status;
    private LocalDate expiresAt;
    private Boolean gdprConsent;
    private LocalDate gdprConsentDate;
    private Map<String, Object> customFieldValues;
}