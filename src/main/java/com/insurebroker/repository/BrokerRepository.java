package com.insurebroker.repository;

import com.insurebroker.entity.Broker;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BrokerRepository extends JpaRepository<Broker, Long> {
    Optional<Broker> findByUserId(Long userId);
}