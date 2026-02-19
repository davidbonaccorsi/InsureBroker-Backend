package com.insurebroker.repository;

import com.insurebroker.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByPerformedByOrderByCreatedAtDesc(Long userId);
}