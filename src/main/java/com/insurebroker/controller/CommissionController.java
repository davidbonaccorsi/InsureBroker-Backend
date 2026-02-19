package com.insurebroker.controller;

import com.insurebroker.dto.response.CommissionResponse;
import com.insurebroker.security.UserPrincipal;
import com.insurebroker.service.CommissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commissions")
@RequiredArgsConstructor
public class CommissionController {
    private final CommissionService commissionService;

    @GetMapping
    public ResponseEntity<List<CommissionResponse>> getCommissions(
            @RequestParam(defaultValue = "false") boolean showAll,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(commissionService.getCommissions(currentUser, showAll));
    }
}