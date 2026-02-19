package com.insurebroker.service;

import com.insurebroker.dto.request.CancelPolicyRequest;
import com.insurebroker.dto.request.PolicyRequest;
import com.insurebroker.dto.response.PolicyResponse;
import com.insurebroker.entity.*;
import com.insurebroker.repository.*;
import com.insurebroker.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final OfferRepository offerRepository;
    private final ClientRepository clientRepository;
    private final BrokerRepository brokerRepository;
    @Value("${file.upload-dir:uploads/}")
    private String uploadDir;

    @Transactional
    public PolicyResponse createPolicy(PolicyRequest request, UserPrincipal currentUser) {
        Broker broker = brokerRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Broker not found"));

        Offer offer = offerRepository.findById(request.getOfferId())
                .orElseThrow(() -> new RuntimeException("Offer not found"));

        if (!offer.getBrokerId().equals(broker.getId()) &&
                currentUser.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRATOR"))) {
            throw new RuntimeException("You do not have permission to convert this offer");
        }

        if ("ACCEPTED".equals(offer.getStatus())) {
            throw new RuntimeException("This offer has already been converted into a policy");
        }

        offer.setStatus("ACCEPTED");
        offerRepository.save(offer);

        String policyNum = "POL-" + LocalDateTime.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        String paymentMethod = request.getPaymentMethod() != null ? request.getPaymentMethod() : "CARD_ONLINE";
        String paymentStatus = "PAID";
        String policyStatus = "ACTIVE";

        if (!"CARD_ONLINE".equals(paymentMethod)) {
            paymentStatus = "PENDING";
            policyStatus = "AWAITING_PAYMENT";
        }

        InsurancePolicy policy = InsurancePolicy.builder()
                .policyNumber(policyNum)
                .offer(offer)
                .offerId(offer.getId())
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
                .status(policyStatus)
                .paymentMethod(paymentMethod)
                .paymentStatus(paymentStatus)
                .gdprConsent(offer.getGdprConsent())
                .gdprConsentDate(offer.getGdprConsentDate())
                .build();

        policy = policyRepository.save(policy);

        Client client = clientRepository.findById(offer.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found for updating stats"));

        if (offer.getGdprConsent() != null && offer.getGdprConsent()) {
            client.setGdprConsent(true);
            if (client.getGdprConsentDate() == null) {
                client.setGdprConsentDate(offer.getGdprConsentDate());
            }
        }

        client.setTotalPolicies((client.getTotalPolicies() == null ? 0 : client.getTotalPolicies()) + 1);
        if ("ACTIVE".equals(policyStatus)) {
            client.setActivePolicies((client.getActivePolicies() == null ? 0 : client.getActivePolicies()) + 1);
        }
        clientRepository.save(client);

        return mapToResponse(policy);
    }

    public List<PolicyResponse> getPolicies(UserPrincipal currentUser, boolean showAll) {
        Broker broker = brokerRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Broker not found"));

        boolean isManagerOrAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRATOR") || a.getAuthority().equals("ROLE_BROKER_MANAGER"));

        List<InsurancePolicy> policies = (isManagerOrAdmin && showAll)
                ? policyRepository.findAll()
                : policyRepository.findByBrokerId(broker.getId());

        return policies.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private PolicyResponse mapToResponse(InsurancePolicy policy) {
        return PolicyResponse.builder()
                .id(policy.getId())
                .policyNumber(policy.getPolicyNumber())
                .offerId(policy.getOfferId())
                .brokerId(policy.getBrokerId())
                .brokerName(policy.getBrokerName())
                .brokerEmail(policy.getBrokerEmail())
                .brokerLicense(policy.getBrokerLicense())
                .clientId(policy.getClientId())
                .clientName(policy.getClientName())
                .productId(policy.getProductId())
                .productName(policy.getProductName())
                .insurerName(policy.getInsurerName())
                .premium(policy.getPremium())
                .sumInsured(policy.getSumInsured())
                .startDate(policy.getStartDate())
                .endDate(policy.getEndDate())
                .status(policy.getStatus())
                .paymentMethod(policy.getPaymentMethod())
                .paymentStatus(policy.getPaymentStatus())
                .cancellationReason(policy.getCancellationReason())
                .proofOfPayment(policy.getProofOfPayment())
                .validatedBy(policy.getValidatedBy())
                .validatedAt(policy.getValidatedAt())
                .gdprConsent(policy.getGdprConsent())
                .gdprConsentDate(policy.getGdprConsentDate())
                .createdAt(policy.getCreatedAt())
                .build();
    }

    @Transactional
    public PolicyResponse updatePolicyStatus(Long id, java.util.Map<String, Object> updates, UserPrincipal currentUser) {
        InsurancePolicy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found"));

        Broker broker = brokerRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Broker not found"));

        if (!policy.getBrokerId().equals(broker.getId()) &&
                currentUser.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRATOR") || a.getAuthority().equals("ROLE_BROKER_MANAGER"))) {
            throw new RuntimeException("You do not have permission to update this policy");
        }

        if (updates.containsKey("status")) {
            policy.setStatus((String) updates.get("status"));
        }

        if (updates.containsKey("paymentStatus")) {
            String newPaymentStatus = (String) updates.get("paymentStatus");
            policy.setPaymentStatus(newPaymentStatus);

            if ("REJECTED".equals(newPaymentStatus) && policy.getProofOfPayment() != null) {
                try {
                    Path filePath = Paths.get(uploadDir).resolve(policy.getProofOfPayment()).normalize();
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    System.err.println("Nu s-a putut sterge fisierul: " + e.getMessage());
                }
                policy.setProofOfPayment(null);
            }
        }

        if (updates.containsKey("proofOfPayment")) {
            policy.setProofOfPayment((String) updates.get("proofOfPayment"));
        }

        if (updates.containsKey("validatedAt")) {
            policy.setValidatedAt((String) updates.get("validatedAt"));
            policy.setValidatedBy(currentUser.getId());
            policy.setStatus("ACTIVE");
            policy.setPaymentStatus("PAID");
        }

        policy = policyRepository.save(policy);
        return mapToResponse(policy);
    }

    @Transactional
    public PolicyResponse cancelPolicy(Long id, CancelPolicyRequest request, UserPrincipal currentUser) {
        InsurancePolicy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found"));

        Broker broker = brokerRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Broker not found"));

        if (!policy.getBrokerId().equals(broker.getId()) &&
                currentUser.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRATOR") || a.getAuthority().equals("ROLE_BROKER_MANAGER"))) {
            throw new RuntimeException("You do not have permission to cancel this policy");
        }

        if ("CANCELLED".equals(policy.getStatus()) || "EXPIRED".equals(policy.getStatus())) {
            throw new RuntimeException("Policy is already cancelled or expired");
        }

        policy.setStatus("CANCELLED");
        policy.setCancellationReason(request.getCancellationReason());
        policy = policyRepository.save(policy);

        Client client = clientRepository.findById(policy.getClientId()).orElse(null);
        if (client != null && client.getActivePolicies() != null && client.getActivePolicies() > 0) {
            client.setActivePolicies(client.getActivePolicies() - 1);
            clientRepository.save(client);
        }

        return mapToResponse(policy);
    }

    @Transactional
    public PolicyResponse uploadPaymentProof(Long id, MultipartFile file, UserPrincipal currentUser) {
        InsurancePolicy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found"));

        if (file.isEmpty()) {
            throw new RuntimeException("Cannot upload empty file");
        }

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown");
            String newFileName = policy.getPolicyNumber() + "_" + originalFileName;

            Path filePath = uploadPath.resolve(newFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            policy.setProofOfPayment(newFileName);
            policy.setPaymentStatus("PENDING");
            policy.setStatus("AWAITING_VALIDATION");

            policy = policyRepository.save(policy);
            return mapToResponse(policy);

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file. Please try again!", ex);
        }
    }

    public org.springframework.http.ResponseEntity<org.springframework.core.io.Resource> downloadPaymentProof(Long id) {
        InsurancePolicy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found"));

        if (policy.getProofOfPayment() == null) {
            throw new RuntimeException("No payment proof found for this policy");
        }

        try {
            Path filePath = Paths.get(uploadDir).resolve(policy.getProofOfPayment()).normalize();
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(filePath.toUri());

            if (resource.exists()) {
                return org.springframework.http.ResponseEntity.ok()
                        .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("File not found on server");
            }
        } catch (java.net.MalformedURLException e) {
            throw new RuntimeException("File path invalid", e);
        }
    }
}