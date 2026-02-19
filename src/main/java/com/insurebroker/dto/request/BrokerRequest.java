package com.insurebroker.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BrokerRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String licenseNumber;
    private BigDecimal commissionRate;
    private LocalDate hireDate;
    private String role;
    private String password;
    private Boolean active;
}