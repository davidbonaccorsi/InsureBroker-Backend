package com.insurebroker.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "activity_type", nullable = false)
    private String activityType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "performed_by", nullable = false)
    private Long performedBy;

    @Column(name = "performed_by_name")
    private String performedByName;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}