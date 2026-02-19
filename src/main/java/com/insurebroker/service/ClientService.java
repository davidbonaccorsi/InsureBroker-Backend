package com.insurebroker.service;

import com.insurebroker.dto.request.ClientRequest;
import com.insurebroker.dto.response.ClientResponse;
import com.insurebroker.entity.Broker;
import com.insurebroker.entity.Client;
import com.insurebroker.repository.BrokerRepository;
import com.insurebroker.repository.ClientRepository;
import com.insurebroker.security.UserPrincipal;
import com.insurebroker.util.CNPValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final BrokerRepository brokerRepository;

    public ClientResponse createClient(ClientRequest request, UserPrincipal currentUser) {
        if (!CNPValidator.isValid(request.getCnp())) {
            throw new RuntimeException("Invalid CNP format or checksum");
        }

        Broker broker = brokerRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Broker profile not found")
        );

        if (request.getCnp() != null && !request.getCnp().isEmpty()) {
            if (clientRepository.findByCnpAndBrokerId(request.getCnp(), broker.getId()).isPresent()) {
                throw new RuntimeException("You have already registered a client with this CNP.");
            }
        }

        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("A client with this email already exists in the system.");
        }

        LocalDate dob = request.getDateOfBirth() != null ?
                request.getDateOfBirth() : CNPValidator.extractDateOfBirth(request.getCnp());

        Client client = Client.builder()
                .brokerId(broker.getId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .cnp(request.getCnp())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .dateOfBirth(dob)
                .nationality(request.getNationality())
                .idType(request.getIdType())
                .idNumber(request.getIdNumber())
                .idExpiry(request.getIdExpiry())
                .gdprConsent(request.getGdprConsent() != null ? request.getGdprConsent() : false)
                .gdprConsentDate(request.getGdprConsentDate())
                .build();

        client = clientRepository.save(client);
        return mapToResponse(client);
    }

    public List<ClientResponse> getClients(UserPrincipal currentUser, boolean showAll) {
        Broker broker = brokerRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Broker profile not found"));

        boolean isManagerOrAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRATOR") || a.getAuthority().equals("ROLE_BROKER_MANAGER"));

        List<Client> clients;
        if (isManagerOrAdmin && showAll) {
            clients = clientRepository.findAll();
        } else {
            clients = clientRepository.findByBrokerId(broker.getId());
        }

        return clients.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private ClientResponse mapToResponse(Client client) {
        return ClientResponse.builder()
                .id(client.getId())
                .brokerId(client.getBrokerId())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .cnp(client.getCnp())
                .email(client.getEmail())
                .phone(client.getPhone())
                .address(client.getAddress())
                .dateOfBirth(client.getDateOfBirth())
                .totalPolicies(client.getTotalPolicies())
                .activePolicies(client.getActivePolicies())
                .createdAt(client.getCreatedAt())
                .updatedAt(client.getUpdatedAt())
                .nationality(client.getNationality())
                .idType(client.getIdType())
                .idNumber(client.getIdNumber())
                .idExpiry(client.getIdExpiry())
                .gdprConsent(client.getGdprConsent())
                .gdprConsentDate(client.getGdprConsentDate())
                .build();
    }
}