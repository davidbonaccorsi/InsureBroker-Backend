package com.insurebroker.repository;
import com.insurebroker.entity.Commission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommissionRepository extends JpaRepository<Commission, Long> {
    List<Commission> findByBrokerId(Long brokerId);
}