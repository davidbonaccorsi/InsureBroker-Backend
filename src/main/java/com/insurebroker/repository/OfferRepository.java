package com.insurebroker.repository;

import com.insurebroker.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findByBrokerId(Long brokerId);
}