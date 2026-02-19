package com.insurebroker.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String code;
    private String description;
    private String category;
    private Long insurerId;
    private String insurerName;

    private BigDecimal basePremium;
    private BigDecimal baseRate;

    private Boolean active;
    private List<CustomFieldDto> customFields;
}