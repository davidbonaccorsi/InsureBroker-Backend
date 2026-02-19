package com.insurebroker.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class CommissionResponse {
    private Long id;
    private Long policyId;
    private String policyNumber;
    private String clientName;
    private BigDecimal amount;
    private LocalDate dueDate;
    private String status;
    private Long brokerId;
}