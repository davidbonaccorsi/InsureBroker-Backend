package com.insurebroker.service;

import com.insurebroker.dto.request.BrokerRequest;
import com.insurebroker.dto.response.BrokerResponse;
import com.insurebroker.entity.*;
import com.insurebroker.repository.BrokerRepository;
import com.insurebroker.repository.UserRepository;
import com.insurebroker.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrokerManagementService {

    private final BrokerRepository brokerRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public List<BrokerResponse> getAllBrokers() {
        return brokerRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public BrokerResponse createBroker(BrokerRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already in use");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setActive(request.getActive() != null ? request.getActive() : true);
        user.setCreatedAt(LocalDateTime.now());
        user = userRepository.save(user);

        UserRoleEntity roleEntity = new UserRoleEntity();
        roleEntity.setUser(user);
        roleEntity.setRole(Role.valueOf(request.getRole()));
        userRoleRepository.save(roleEntity);

        Broker broker = Broker.builder()
                .user(user)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .licenseNumber(request.getLicenseNumber())
                .commissionRate(request.getCommissionRate())
                .hireDate(request.getHireDate())
                .role(Role.valueOf(request.getRole()))
                .active(user.getActive())
                .build();

        broker = brokerRepository.save(broker);
        return mapToResponse(broker);
    }

    @Transactional
    public BrokerResponse updateBroker(Long id, BrokerRequest request) {
        Broker broker = brokerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Broker not found")
        );

        User user = userRepository.findById(broker.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found")
        );

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setActive(request.getActive());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        userRepository.save(user);

        if (!broker.getRole().name().equals(request.getRole())) {
            UserRoleEntity roleEntity = userRoleRepository.findByUserId(user.getId())
                    .stream().findFirst().orElse(new UserRoleEntity());
            roleEntity.setUser(user);
            roleEntity.setRole(Role.valueOf(request.getRole()));
            userRoleRepository.save(roleEntity);
        }

        broker.setFirstName(request.getFirstName());
        broker.setLastName(request.getLastName());
        broker.setPhone(request.getPhone());
        broker.setLicenseNumber(request.getLicenseNumber());
        broker.setCommissionRate(request.getCommissionRate());
        broker.setHireDate(request.getHireDate());
        broker.setRole(Role.valueOf(request.getRole()));
        broker.setActive(request.getActive());

        broker = brokerRepository.save(broker);
        return mapToResponse(broker);
    }

    @Transactional
    public void deactivateBroker(Long id) {
        Broker broker = brokerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Broker not found"));

        broker.setActive(false);
        brokerRepository.save(broker);

        userRepository.findById(broker.getUser().getId()).ifPresent(user -> {
            user.setActive(false);
            userRepository.save(user);
        });
    }

    private BrokerResponse mapToResponse(Broker broker) {
        return BrokerResponse.builder()
                .id(broker.getId())
                .userId(broker.getUser() != null ? broker.getUser().getId() : null)
                .firstName(broker.getFirstName())
                .lastName(broker.getLastName())
                .email(broker.getEmail())
                .phone(broker.getPhone())
                .licenseNumber(broker.getLicenseNumber())
                .commissionRate(broker.getCommissionRate())
                .hireDate(broker.getHireDate())
                .role(broker.getRole().name())
                .active(broker.getActive())
                .build();
    }
}