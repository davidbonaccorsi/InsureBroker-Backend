package com.insurebroker.repository;

import com.insurebroker.entity.InsurancePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PolicyRepository extends JpaRepository<InsurancePolicy, Long> {
    List<InsurancePolicy> findByBrokerId(Long brokerId);
}