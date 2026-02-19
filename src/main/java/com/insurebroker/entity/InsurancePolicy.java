package com.insurebroker.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "insurance_policies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsurancePolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_number", unique = true, nullable = false)
    private String policyNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", insertable = false, updatable = false)
    private Offer offer;

    @Column(name = "offer_id")
    private Long offerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", insertable = false, updatable = false)
    private Client client;

    @Column(name = "client_id")
    private Long clientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private InsuranceProduct product;

    @Column(name = "product_id")
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "broker_id", insertable = false, updatable = false)
    private Broker broker;

    @Column(name = "broker_id")
    private Long brokerId;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "insurer_name")
    private String insurerName;

    @Column(name = "broker_name")
    private String brokerName;

    @Column(name = "broker_email")
    private String brokerEmail;

    @Column(name = "broker_license")
    private String brokerLicense;

    @Column(name = "gdpr_consent")
    private Boolean gdprConsent;

    @Column(name = "gdpr_consent_date")
    private LocalDate gdprConsentDate;

    @Column(name = "premium", nullable = false)
    private BigDecimal premium;

    @Column(name = "sum_insured")
    private BigDecimal sumInsured;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "status", nullable = false)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "payment_status")
    @Builder.Default
    private String paymentStatus = "PAID";

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @Column(name = "proof_of_payment")
    private String proofOfPayment;

    @Column(name = "validated_by")
    private Long validatedBy;

    @Column(name = "validated_at")
    private String validatedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}