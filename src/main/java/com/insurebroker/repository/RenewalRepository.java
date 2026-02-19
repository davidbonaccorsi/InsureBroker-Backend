package com.insurebroker.repository;
import com.insurebroker.entity.Renewal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RenewalRepository extends JpaRepository<Renewal, Long> {
    List<Renewal> findByBrokerId(Long brokerId);
}