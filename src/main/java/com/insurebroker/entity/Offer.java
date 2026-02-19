package com.insurebroker.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "offers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "offer_number")
    private String offerNumber;

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

    private String clientName;
    private String productName;
    private String insurerName;

    @Column(name = "base_premium", nullable = false)
    private BigDecimal basePremium;

    @Column(name = "final_premium", nullable = false)
    private BigDecimal finalPremium;

    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal sumInsured;
    private LocalDate validUntil;
    private LocalDateTime createdAt;

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

    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OfferCustomFieldValue> customFieldValues = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}