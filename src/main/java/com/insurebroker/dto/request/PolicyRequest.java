package com.insurebroker.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
public class PolicyRequest {
    @NotNull(message = "Offer ID is required")
    private Long offerId;

    private LocalDate startDate;
    private LocalDate endDate;
    private String paymentMethod;

    private Long clientId;
    private Long productId;
    private Long brokerId;
    private String policyNumber;
    private BigDecimal premium;
    private BigDecimal sumInsured;
    private String status;
    private String paymentStatus;
    private Map<String, Object> customFieldValues;
}