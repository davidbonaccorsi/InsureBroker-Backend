package com.insurebroker.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "offer_custom_field_values")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferCustomFieldValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", nullable = false)
    private Offer offer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id")
    private ProductCustomField customField;

    @Column(name = "field_name")
    private String fieldName;

    @Column(name = "field_value")
    private String fieldValue;
}