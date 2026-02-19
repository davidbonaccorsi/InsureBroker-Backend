package com.insurebroker.controller;

import com.insurebroker.dto.request.ClientRequest;
import com.insurebroker.dto.response.ClientResponse;
import com.insurebroker.security.UserPrincipal;
import com.insurebroker.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientResponse> createClient(
            @Valid @RequestBody ClientRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        ClientResponse response = clientService.createClient(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ClientResponse>> getClients(
            @RequestParam(defaultValue = "false") boolean showAll,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        List<ClientResponse> responses = clientService.getClients(currentUser, showAll);
        return ResponseEntity.ok(responses);
    }
}