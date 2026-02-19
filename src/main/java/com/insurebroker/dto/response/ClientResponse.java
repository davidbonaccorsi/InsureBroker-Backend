package com.insurebroker.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ClientResponse {
    private Long id;
    private Long brokerId;
    private String firstName;
    private String lastName;
    private String cnp;
    private String email;
    private String phone;
    private String address;
    private LocalDate dateOfBirth;
    private Integer totalPolicies;
    private Integer activePolicies;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String nationality;
    private String idType;
    private String idNumber;
    private LocalDate idExpiry;
    private Boolean gdprConsent;
    private LocalDate gdprConsentDate;
}