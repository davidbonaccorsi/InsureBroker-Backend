package com.insurebroker.repository;

import com.insurebroker.entity.Insurer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsurerRepository extends JpaRepository<Insurer, Long> {
}