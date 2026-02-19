package com.insurebroker.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class BrokerResponse {
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String licenseNumber;
    private BigDecimal commissionRate;
    private LocalDate hireDate;
    private String role;
    private Boolean active;
}