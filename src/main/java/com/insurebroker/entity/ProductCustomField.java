package com.insurebroker.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product_custom_fields")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCustomField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private InsuranceProduct product;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private String type;

    @Builder.Default
    private Boolean required = false;

    @Column(columnDefinition = "JSON")
    private String options;

    private String placeholder;

    @Column(name = "factor_multiplier", precision = 5, scale = 2)
    private BigDecimal factorMultiplier;

    @Column(name = "factor_condition")
    private String factorCondition;
}