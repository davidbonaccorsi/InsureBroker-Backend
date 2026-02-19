package com.insurebroker.service;

import com.insurebroker.dto.request.PremiumRequest;
import com.insurebroker.dto.response.PremiumResponse;
import com.insurebroker.entity.InsuranceProduct;
import com.insurebroker.entity.ProductCustomField;
import com.insurebroker.repository.ProductCustomFieldRepository;
import com.insurebroker.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PremiumCalculationService {

    private final ProductRepository productRepository;
    private final ProductCustomFieldRepository customFieldRepository;

    public PremiumResponse calculatePremium(PremiumRequest request) {
        InsuranceProduct product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        long days = 365;
        if (request.getStartDate() != null && request.getEndDate() != null) {
            try {
                java.time.LocalDate start = java.time.LocalDate.parse(request.getStartDate());
                java.time.LocalDate end = java.time.LocalDate.parse(request.getEndDate());
                days = java.time.temporal.ChronoUnit.DAYS.between(start, end);

                if (days <= 0) days = 1;
            } catch (Exception e) {
                days = 365;
            }
        }

        BigDecimal baseFixedPremium = product.getBasePremium() != null ? product.getBasePremium() : BigDecimal.ZERO;
        BigDecimal dailyRatePercentage = product.getBaseRate() != null ? product.getBaseRate() : BigDecimal.ZERO;

        BigDecimal dailyPremium = request.getSumInsured().multiply(dailyRatePercentage);
        BigDecimal variablePremium = dailyPremium.multiply(BigDecimal.valueOf(days));

        BigDecimal basePremium = baseFixedPremium.add(variablePremium);

        double totalMultiplier = 1.0;
        List<PremiumResponse.Factor> appliedFactors = new ArrayList<>();

        if (baseFixedPremium.compareTo(BigDecimal.ZERO) > 0) {
            appliedFactors.add(PremiumResponse.Factor.builder()
                    .name("Fixed Base Premium")
                    .multiplier(1.0)
                    .reason("Starting cost: $" + baseFixedPremium)
                    .build());
        }

        if (dailyRatePercentage.compareTo(BigDecimal.ZERO) > 0) {
            appliedFactors.add(PremiumResponse.Factor.builder()
                    .name("Duration Risk")
                    .multiplier(1.0)
                    .reason(days + " days covered")
                    .build());
        }

        List<ProductCustomField> customFields = customFieldRepository.findByProductId(product.getId());
        Map<String, Object> submittedValues = request.getCustomFieldValues();

        if (submittedValues != null) {
            for (ProductCustomField field : customFields) {
                if (field.getFactorMultiplier() != null) {
                    Object value = submittedValues.get(field.getName());
                    if (value != null) {
                        boolean conditionMet = field.getFactorCondition() != null && evaluateCondition(String.valueOf(value), field.getFactorCondition());
                        String fieldType = field.getType() != null ? field.getType() : "text";

                        if (conditionMet || fieldType.equals("select") || fieldType.equals("checkbox") || fieldType.equals("number")) {
                            double fieldMultiplier = field.getFactorMultiplier().doubleValue();
                            double appliedMultiplier = 1.0;

                            if (fieldType.equals("checkbox")) {
                                if (!Boolean.parseBoolean(String.valueOf(value))) continue;
                                appliedMultiplier = fieldMultiplier;
                            } else if (fieldType.equals("select")) {
                                if (field.getFactorCondition() != null && !String.valueOf(value).equalsIgnoreCase(field.getFactorCondition())) continue;
                                appliedMultiplier = fieldMultiplier;
                            } else if (fieldType.equals("number")) {
                                try {
                                    int quantity = Integer.parseInt(String.valueOf(value));
                                    if (quantity <= 0) continue;
                                    appliedMultiplier = Math.pow(fieldMultiplier, quantity);
                                } catch (Exception e) {
                                    continue;
                                }
                            } else if (conditionMet) {
                                appliedMultiplier = fieldMultiplier;
                            }

                            totalMultiplier *= appliedMultiplier;

                            appliedFactors.add(PremiumResponse.Factor.builder()
                                    .name(field.getLabel() + (fieldType.equals("number") ? " (" + value + ")" : ""))
                                    .multiplier(appliedMultiplier)
                                    .reason(fieldType.equals("number") ? "Multiplied by quantity" : "Custom modifier applied")
                                    .build());
                        }
                    }
                }
            }
        }

        BigDecimal finalPremium = basePremium.multiply(BigDecimal.valueOf(totalMultiplier))
                .setScale(2, RoundingMode.HALF_UP);

        return PremiumResponse.builder()
                .premium(finalPremium)
                .breakdown(PremiumResponse.Breakdown.builder()
                        .basePremium(basePremium.setScale(2, RoundingMode.HALF_UP))
                        .factors(appliedFactors)
                        .build())
                .build();
    }

    private boolean evaluateCondition(String submittedValue, String condition) {
        try {
            if (condition.contains("===")) {
                String expected = condition.split("===")[1].trim().replace("\"", "").replace("'", "");
                return submittedValue.equalsIgnoreCase(expected);
            } else if (condition.contains("<")) {
                double threshold = Double.parseDouble(condition.split("<")[1].trim());
                return Double.parseDouble(submittedValue) < threshold;
            } else if (condition.contains(">")) {
                double threshold = Double.parseDouble(condition.split(">")[1].trim());
                return Double.parseDouble(submittedValue) > threshold;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}