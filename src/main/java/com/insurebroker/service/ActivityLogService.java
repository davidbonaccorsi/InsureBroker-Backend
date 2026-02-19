package com.insurebroker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurebroker.dto.request.ActivityLogRequest;
import com.insurebroker.entity.ActivityLog;
import com.insurebroker.repository.ActivityLogRepository;
import com.insurebroker.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository repository;
    private final ObjectMapper objectMapper;

    public ActivityLog logActivity(ActivityLogRequest request) {
        String metadataJson = null;
        try {
            if (request.getMetadata() != null) {
                metadataJson = objectMapper.writeValueAsString(request.getMetadata());
            }
        } catch (JsonProcessingException e) {
        }

        ActivityLog log = ActivityLog.builder()
                .entityType(request.getEntityType())
                .entityId(request.getEntityId())
                .activityType(request.getActivityType())
                .description(request.getDescription())
                .performedBy(request.getPerformedBy())
                .performedByName(request.getPerformedByName())
                .metadata(metadataJson)
                .build();

        return repository.save(log);
    }

    public List<ActivityLog> getLogs(UserPrincipal currentUser, boolean showAll) {
        boolean isManagerOrAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRATOR") || a.getAuthority().equals("ROLE_BROKER_MANAGER"));

        if (isManagerOrAdmin && showAll) {
            return repository.findAll();
        }
        return repository.findByPerformedByOrderByCreatedAtDesc(currentUser.getId());
    }
}