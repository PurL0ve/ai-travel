package com.ai.travel.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * AI 综合推荐响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendResponse {

    /**
     * 目的地
     */
    private String destination;

    /**
     * 行程天数
     */
    private Integer days;

    /**
     * 预算
     */
    private String budget;

    /**
     * AI 推荐的多个行程方案
     */
    private List<PlanOption> plans;

    /**
     * 数据库中匹配的旅游产品
     */
    private List<MatchedProduct> products;

    // ---- 行程方案 ----
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanOption {
        /** 方案主题，如"文化深度游" */
        private String theme;
        /** 方案简介 */
        private String description;
        /** 预估费用 */
        private Double estimatedCost;
        /** 每日行程 */
        private List<DayItem> schedule;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayItem {
        private Integer day;
        private String morning;
        private String afternoon;
        private String evening;
        private String highlight;
        private String tip;
    }

    // ---- 匹配产品 ----
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchedProduct {
        private Long productId;
        private String productName;
        private String type;
        private String destination;
        private Integer days;
        private BigDecimal price;
        private String tags;
        private String description;
        private Double matchScore;
    }
}