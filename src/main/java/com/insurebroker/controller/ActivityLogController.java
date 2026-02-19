package com.insurebroker.controller;

import com.insurebroker.dto.request.ActivityLogRequest;
import com.insurebroker.entity.ActivityLog;
import com.insurebroker.security.UserPrincipal;
import com.insurebroker.service.ActivityLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity-logs")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService service;

    @PostMapping
    public ResponseEntity<ActivityLog> createLog(@Valid @RequestBody ActivityLogRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.logActivity(request));
    }

    @GetMapping
    public ResponseEntity<List<ActivityLog>> getLogs(
            @RequestParam(defaultValue = "false") boolean showAll,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(service.getLogs(currentUser, showAll));
    }
}