package com.insurebroker.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class CustomFieldDto {
    private String id;
    private String name;
    private String label;
    private String type;
    private Boolean required;
    private List<String> options;
    private String placeholder;
    private Double factorMultiplier;
    private String factorCondition;
}