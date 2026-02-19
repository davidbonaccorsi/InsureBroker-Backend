package com.insurebroker.service;

import com.insurebroker.entity.Notification;
import com.insurebroker.repository.NotificationRepository;
import com.insurebroker.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<Notification> getNotifications(UserPrincipal currentUser) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId());
    }

    public void markAsRead(Long id, UserPrincipal currentUser) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (notification.getUserId().equals(currentUser.getId()) ||
                currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRATOR"))) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }
}