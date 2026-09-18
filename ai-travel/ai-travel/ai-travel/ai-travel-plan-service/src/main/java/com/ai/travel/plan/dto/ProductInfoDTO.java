package com.ai.travel.plan.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductInfoDTO {
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
}