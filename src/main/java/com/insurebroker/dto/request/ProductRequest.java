package com.insurebroker.dto.request;

import com.insurebroker.dto.response.CustomFieldDto;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductRequest {
    private String name;
    private String code;
    private String description;
    private String category;
    private Long insurerId;

    private BigDecimal basePremium;
    private BigDecimal baseRate;

    private Boolean active;
    private List<CustomFieldDto> customFields;
}