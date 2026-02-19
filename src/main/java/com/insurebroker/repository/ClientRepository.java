package com.insurebroker.repository;

import com.insurebroker.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByBrokerId(Long brokerId);
    boolean existsByCnp(String cnp);
    boolean existsByEmail(String email);
    Optional<Client> findByCnpAndBrokerId(String cnp, Long brokerId);
}