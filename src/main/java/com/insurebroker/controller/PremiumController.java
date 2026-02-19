package com.insurebroker.controller;

import com.insurebroker.dto.request.PremiumRequest;
import com.insurebroker.dto.response.PremiumResponse;
import com.insurebroker.service.PremiumCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/premium")
@RequiredArgsConstructor
public class PremiumController {

    private final PremiumCalculationService premiumService;

    @PostMapping("/calculate")
    public ResponseEntity<PremiumResponse> calculatePremium(@RequestBody PremiumRequest request) {
        return ResponseEntity.ok(premiumService.calculatePremium(request));
    }
}