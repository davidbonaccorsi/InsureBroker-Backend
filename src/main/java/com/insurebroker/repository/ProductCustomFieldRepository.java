package com.insurebroker.repository;

import com.insurebroker.entity.ProductCustomField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductCustomFieldRepository extends JpaRepository<ProductCustomField, Long> {
    List<ProductCustomField> findByProductId(Long productId);

    @Transactional
    void deleteByProductId(Long productId);
}