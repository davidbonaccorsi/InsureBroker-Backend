package com.insurebroker.service;

import com.insurebroker.dto.response.CommissionResponse;
import com.insurebroker.entity.Broker;
import com.insurebroker.entity.Commission;
import com.insurebroker.repository.BrokerRepository;
import com.insurebroker.repository.CommissionRepository;
import com.insurebroker.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommissionService {

    private final CommissionRepository commissionRepository;
    private final BrokerRepository brokerRepository;

    public List<CommissionResponse> getCommissions(UserPrincipal currentUser, boolean showAll) {
        Broker broker = brokerRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Broker not found"));

        boolean isManagerOrAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRATOR") || a.getAuthority().equals("ROLE_BROKER_MANAGER"));

        List<Commission> commissions = (isManagerOrAdmin && showAll)
                ? commissionRepository.findAll()
                : commissionRepository.findByBrokerId(broker.getId());

        return commissions.stream().map(c -> CommissionResponse.builder()
                .id(c.getId())
                .policyId(c.getPolicy().getId())
                .policyNumber(c.getPolicy().getPolicyNumber())
                .clientName(c.getPolicy().getClient().getFirstName() + " " + c.getPolicy().getClient().getLastName())
                .amount(c.getAmount())
                .dueDate(c.getDueDate())
                .status(c.getStatus())
                .brokerId(c.getBrokerId())
                .build()).collect(Collectors.toList());
    }
}