package com.insurebroker.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Map;

@Data
public class ActivityLogRequest {
    @NotBlank
    private String entityType;
    @NotNull
    private Long entityId;
    @NotBlank
    private String activityType;
    private String description;
    @NotNull
    private Long performedBy;
    private String performedByName;
    private Map<String, Object> metadata;
}