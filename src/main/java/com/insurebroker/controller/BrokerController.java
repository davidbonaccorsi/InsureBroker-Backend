package com.insurebroker.controller;

import com.insurebroker.dto.request.BrokerRequest;
import com.insurebroker.dto.response.BrokerResponse;
import com.insurebroker.service.BrokerManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brokers")
@RequiredArgsConstructor
public class BrokerController {

    private final BrokerManagementService brokerService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'BROKER_MANAGER')")
    public ResponseEntity<List<BrokerResponse>> getAllBrokers() {
        return ResponseEntity.ok(brokerService.getAllBrokers());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<BrokerResponse> createBroker(@RequestBody BrokerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(brokerService.createBroker(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<BrokerResponse> updateBroker(@PathVariable Long id, @RequestBody BrokerRequest request) {
        return ResponseEntity.ok(brokerService.updateBroker(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Void> deactivateBroker(@PathVariable Long id) {
        brokerService.deactivateBroker(id);
        return ResponseEntity.noContent().build();
    }
}