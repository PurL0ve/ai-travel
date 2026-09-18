package com.ai.travel.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;

    private String name;

    private String type;

    private String destination;

    private Integer days;

    private BigDecimal price;

    private Integer stock;

    private String description;

    private String includes;

    private String tags;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}