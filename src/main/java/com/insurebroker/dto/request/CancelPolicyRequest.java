package com.insurebroker.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CancelPolicyRequest {
    @NotBlank(message = "Cancellation reason is strictly required")
    private String cancellationReason;
}