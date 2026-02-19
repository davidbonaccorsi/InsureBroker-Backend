package com.insurebroker.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "renewals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Renewal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "broker_id", nullable = false)
    private Long brokerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private InsurancePolicy policy;

    @Column(name = "renewal_date", nullable = false)
    private LocalDate renewalDate;

    @Column(name = "new_premium", precision = 10, scale = 2)
    private BigDecimal newPremium;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}