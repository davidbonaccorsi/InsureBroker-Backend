package com.insurebroker.controller;

import com.insurebroker.entity.Notification;
import com.insurebroker.security.UserPrincipal;
import com.insurebroker.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(notificationService.getNotifications(currentUser));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        notificationService.markAsRead(id, currentUser);
        return ResponseEntity.ok().build();
    }
}