package com.insurebroker.repository;

import com.insurebroker.entity.InsuranceProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<InsuranceProduct, Long> {}