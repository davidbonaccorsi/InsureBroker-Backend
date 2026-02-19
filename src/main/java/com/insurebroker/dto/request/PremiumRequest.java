package com.insurebroker.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class PremiumRequest {
    private Long productId;
    private BigDecimal sumInsured;
    private String startDate;
    private String endDate;

    private Map<String, Object> customFieldValues;
    private ClientData clientData;

    @Data
    public static class ClientData {
        private String cnp;
        private String dateOfBirth;
    }
}