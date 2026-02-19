package com.insurebroker.service;

import com.insurebroker.dto.response.RenewalResponse;
import com.insurebroker.entity.Broker;
import com.insurebroker.entity.Renewal;
import com.insurebroker.repository.BrokerRepository;
import com.insurebroker.repository.RenewalRepository;
import com.insurebroker.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RenewalService {

    private final RenewalRepository renewalRepository;
    private final BrokerRepository brokerRepository;

    public List<RenewalResponse> getRenewals(UserPrincipal currentUser, boolean showAll) {
        Broker broker = brokerRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Broker not found"));

        boolean isManagerOrAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRATOR") || a.getAuthority().equals("ROLE_BROKER_MANAGER"));

        List<Renewal> renewals = (isManagerOrAdmin && showAll)
                ? renewalRepository.findAll()
                : renewalRepository.findByBrokerId(broker.getId());

        return renewals.stream().map(r -> RenewalResponse.builder()
                .id(r.getId())
                .policyId(r.getPolicy().getId())
                .policyNumber(r.getPolicy().getPolicyNumber())
                .clientName(r.getPolicy().getClient().getFirstName() + " " + r.getPolicy().getClient().getLastName())
                .renewalDate(r.getRenewalDate())
                .newPremium(r.getNewPremium())
                .status(r.getStatus())
                .brokerId(r.getBrokerId())
                .build()).collect(Collectors.toList());
    }
}