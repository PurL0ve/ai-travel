package com.ai.travel.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductMatchResponse {

    private String destination;

    private Integer days;

    private List<MatchedProduct> products;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchedProduct {
        private Long productId;
        private String productName;
        private String type;
        private BigDecimal price;
        private String destination;
        private Integer days;
        private String tags;
        private Double matchScore;
        private String description;
    }

}