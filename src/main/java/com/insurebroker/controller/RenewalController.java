package com.insurebroker.controller;

import com.insurebroker.dto.response.RenewalResponse;
import com.insurebroker.security.UserPrincipal;
import com.insurebroker.service.RenewalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/renewals")
@RequiredArgsConstructor
public class RenewalController {
    private final RenewalService renewalService;

    @GetMapping
    public ResponseEntity<List<RenewalResponse>> getRenewals(
            @RequestParam(defaultValue = "false") boolean showAll,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(renewalService.getRenewals(currentUser, showAll));
    }
}