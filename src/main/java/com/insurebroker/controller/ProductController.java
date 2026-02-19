package com.insurebroker.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurebroker.dto.request.ProductRequest;
import com.insurebroker.dto.response.CustomFieldDto;
import com.insurebroker.dto.response.ProductResponse;
import com.insurebroker.entity.InsuranceProduct;
import com.insurebroker.entity.ProductCustomField;
import com.insurebroker.repository.InsurerRepository;
import com.insurebroker.repository.ProductCustomFieldRepository;
import com.insurebroker.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final InsurerRepository insurerRepository;
    private final ProductCustomFieldRepository customFieldRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> responses = productRepository.findAll().stream().map(product -> {
            String insurerName = insurerRepository.findById(product.getInsurerId())
                    .map(insurer -> insurer.getName())
                    .orElse("Unknown Insurer");

            List<CustomFieldDto> customFields = customFieldRepository.findByProductId(product.getId())
                    .stream().map(field -> {
                        List<String> optionsList = new ArrayList<>();
                        if (field.getOptions() != null && !field.getOptions().isEmpty()) {
                            try {
                                optionsList = objectMapper.readValue(field.getOptions(), new TypeReference<List<String>>(){});
                            } catch (Exception e) {}
                        }

                        return CustomFieldDto.builder()
                                .id(String.valueOf(field.getId()))
                                .name(field.getName())
                                .label(field.getLabel())
                                .type(field.getType())
                                .required(field.getRequired())
                                .options(optionsList.isEmpty() ? null : optionsList)
                                .placeholder(field.getPlaceholder())
                                .factorMultiplier(field.getFactorMultiplier() != null ? field.getFactorMultiplier().doubleValue() : null)
                                .factorCondition(field.getFactorCondition())
                                .build();
                    }).collect(Collectors.toList());

            return ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .code(product.getCode())
                    .description(product.getDescription())
                    .category(product.getCategory())
                    .insurerId(product.getInsurerId())
                    .insurerName(insurerName)
                    .basePremium(product.getBasePremium())
                    .baseRate(product.getBaseRate())
                    .active(product.getActive())
                    .customFields(customFields)
                    .build();
        }).collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        InsuranceProduct product = new InsuranceProduct();

        updateProductFromRequest(product, request);

        InsuranceProduct saved = productRepository.save(product);
        saveCustomFields(saved, request.getCustomFields());

        return ResponseEntity.ok(mapToProductResponse(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        InsuranceProduct product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        updateProductFromRequest(product, request);

        InsuranceProduct saved = productRepository.save(product);
        saveCustomFields(saved, request.getCustomFields());

        return ResponseEntity.ok(mapToProductResponse(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    private void updateProductFromRequest(InsuranceProduct product, ProductRequest request) {
        if (request.getName() != null) product.setName(request.getName());
        if (request.getCode() != null) product.setCode(request.getCode());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getCategory() != null) product.setCategory(request.getCategory());
        if (request.getInsurerId() != null) product.setInsurerId(request.getInsurerId());

        product.setBasePremium(request.getBasePremium() != null ? request.getBasePremium() : BigDecimal.ZERO);
        product.setBaseRate(request.getBaseRate() != null ? request.getBaseRate() : BigDecimal.ZERO);

        if (request.getActive() != null) product.setActive(request.getActive());
    }

    private ProductResponse mapToProductResponse(InsuranceProduct product) {
        String insurerName = insurerRepository.findById(product.getInsurerId())
                .map(i -> i.getName())
                .orElse("Unknown Insurer");

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .code(product.getCode())
                .description(product.getDescription())
                .category(product.getCategory())
                .insurerId(product.getInsurerId())
                .insurerName(insurerName)
                .basePremium(product.getBasePremium())
                .baseRate(product.getBaseRate())
                .active(product.getActive())
                .build();
    }

    private void saveCustomFields(InsuranceProduct product, List<CustomFieldDto> fieldsDto) {
        if (fieldsDto == null) return;

        customFieldRepository.deleteByProductId(product.getId());

        for (CustomFieldDto dto : fieldsDto) {
            ProductCustomField field = new ProductCustomField();
            field.setProduct(product);
            field.setName(dto.getName());
            field.setLabel(dto.getLabel());
            field.setType(dto.getType());
            field.setRequired(dto.getRequired());
            field.setPlaceholder(dto.getPlaceholder());
            field.setFactorMultiplier(dto.getFactorMultiplier() != null ? BigDecimal.valueOf(dto.getFactorMultiplier()) : null);
            field.setFactorCondition(dto.getFactorCondition());

            if (dto.getOptions() != null && !dto.getOptions().isEmpty()) {
                try {
                    field.setOptions(objectMapper.writeValueAsString(dto.getOptions()));
                } catch (Exception e) {}
            }
            customFieldRepository.save(field);
        }
    }
}