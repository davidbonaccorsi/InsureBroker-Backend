package com.insurebroker.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class PremiumResponse {
    private BigDecimal premium;
    private Breakdown breakdown;

    @Data
    @Builder
    public static class Breakdown {
        private BigDecimal basePremium;
        private List<Factor> factors;
    }

    @Data
    @Builder
    public static class Factor {
        private String name;
        private Double multiplier;
        private String reason;
    }
}