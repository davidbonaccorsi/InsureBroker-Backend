package com.insurebroker.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ClientRequest {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "CNP is required")
    private String cnp;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    private String phone;
    private String address;
    private LocalDate dateOfBirth;
    private Long brokerId;
    private String nationality;
    private String idType;
    private String idNumber;
    private LocalDate idExpiry;
    private Boolean gdprConsent;
    private LocalDate gdprConsentDate;
}