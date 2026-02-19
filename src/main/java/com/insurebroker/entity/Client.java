package com.insurebroker.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "clients", indexes = {
        @Index(name = "idx_clients_email", columnList = "email"),
        @Index(name = "idx_clients_cnp", columnList = "cnp"),
        @Index(name = "idx_clients_broker", columnList = "broker_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "broker_id", nullable = false)
    private Long brokerId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, length = 13)
    private String cnp;

    @Column(nullable = false)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "total_policies")
    @Builder.Default
    private Integer totalPolicies = 0;

    @Column(name = "active_policies")
    @Builder.Default
    private Integer activePolicies = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "id_type")
    private String idType;

    @Column(name = "id_number")
    private String idNumber;

    @Column(name = "id_expiry")
    private LocalDate idExpiry;

    @Column(name = "gdpr_consent")
    private Boolean gdprConsent;

    @Column(name = "gdpr_consent_date")
    private LocalDate gdprConsentDate;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}