package com.insurebroker.controller;

import com.insurebroker.dto.request.OfferRequest;
import com.insurebroker.dto.response.OfferResponse;
import com.insurebroker.security.UserPrincipal;
import com.insurebroker.service.OfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;

    @PostMapping
    public ResponseEntity<OfferResponse> createOffer(
            @Valid @RequestBody OfferRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        OfferResponse response = offerService.createOffer(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<OfferResponse>> getOffers(
            @RequestParam(defaultValue = "false") boolean showAll,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<OfferResponse> responses = offerService.getOffers(currentUser, showAll);
        return ResponseEntity.ok(responses);
    }
}