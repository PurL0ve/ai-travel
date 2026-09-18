package com.ai.travel.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductQueryRequest {

    private String type;

    private String destination;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    public void check() {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        if (pageSize > 100) {
            pageSize = 100;
        }
    }

}