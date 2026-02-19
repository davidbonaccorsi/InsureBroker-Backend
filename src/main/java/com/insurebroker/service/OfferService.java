package com.insurebroker.service;

import com.insurebroker.dto.request.OfferRequest;
import com.insurebroker.dto.response.OfferResponse;
import com.insurebroker.entity.*;
import com.insurebroker.repository.*;
import com.insurebroker.security.UserPrincipal;
import com.insurebroker.util.CNPValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OfferService {

    private final OfferRepository offerRepository;
    private final BrokerRepository brokerRepository;
    private final OfferCustomFieldValueRepository customFieldValueRepository;

    @Transactional
    public OfferResponse createOffer(OfferRequest request, UserPrincipal currentUser) {

        Broker currentBroker = brokerRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Broker profile not found"));

        if (request.getCustomFieldValues() != null && request.getCustomFieldValues().containsKey("beneficiaryCnp")) {
            String bCnp = String.valueOf(request.getCustomFieldValues().get("beneficiaryCnp"));
            if (bCnp != null && !bCnp.isEmpty() && !CNPValidator.isValid(bCnp)) {
                throw new RuntimeException("CNP-ul Beneficiarului este invalid!");
            }
        }

        LocalDate expirationDate = request.getExpiresAt() != null ? request.getExpiresAt() : LocalDate.now().plusDays(30);

        Offer offer = Offer.builder()
                .offerNumber("OFR-" + System.currentTimeMillis())
                .clientId(request.getClientId())
                .clientName(request.getClientName())
                .productId(request.getProductId())
                .productName(request.getProductName())
                .insurerName(request.getInsurerName())
                .brokerId(currentBroker.getId())
                .brokerName(currentBroker.getFirstName() + " " + currentBroker.getLastName())
                .brokerEmail(currentBroker.getEmail())
                .brokerLicense(currentBroker.getLicenseNumber())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .sumInsured(request.getSumInsured())
                .basePremium(request.getPremium())
                .finalPremium(request.getPremium())
                .status(request.getStatus() != null ? request.getStatus() : "PENDING")
                .validUntil(expirationDate)
                .gdprConsent(request.getGdprConsent())
                .gdprConsentDate(request.getGdprConsentDate())
                .build();

        offer = offerRepository.save(offer);

        if (request.getCustomFieldValues() != null) {
            for (Map.Entry<String, Object> entry : request.getCustomFieldValues().entrySet()) {
                OfferCustomFieldValue val = OfferCustomFieldValue.builder()
                        .offer(offer)
                        .customField(null)
                        .fieldName(entry.getKey())
                        .fieldValue(String.valueOf(entry.getValue()))
                        .build();
                customFieldValueRepository.save(val);
            }
        }

        return mapToResponse(offer);
    }

    public List<OfferResponse> getOffers(UserPrincipal currentUser, boolean showAll) {
        Broker broker = brokerRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Broker not found"));

        boolean isManagerOrAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRATOR") || a.getAuthority().equals("ROLE_BROKER_MANAGER"));

        List<Offer> offers = (isManagerOrAdmin && showAll)
                ? offerRepository.findAll()
                : offerRepository.findByBrokerId(broker.getId());

        return offers.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private OfferResponse mapToResponse(Offer offer) {
        return OfferResponse.builder()
                .id(offer.getId())
                .offerNumber(offer.getOfferNumber())
                .brokerId(offer.getBrokerId())
                .brokerName(offer.getBrokerName())
                .brokerEmail(offer.getBrokerEmail())
                .brokerLicense(offer.getBrokerLicense())
                .clientId(offer.getClientId())
                .clientName(offer.getClientName())
                .productId(offer.getProductId())
                .productName(offer.getProductName())
                .insurerName(offer.getInsurerName())
                .premium(offer.getFinalPremium())
                .sumInsured(offer.getSumInsured())
                .startDate(offer.getStartDate())
                .endDate(offer.getEndDate())
                .status(offer.getStatus())
                .expiresAt(offer.getValidUntil())
                .gdprConsent(offer.getGdprConsent())
                .gdprConsentDate(offer.getGdprConsentDate())
                .createdAt(offer.getCreatedAt())
                .build();
    }
}