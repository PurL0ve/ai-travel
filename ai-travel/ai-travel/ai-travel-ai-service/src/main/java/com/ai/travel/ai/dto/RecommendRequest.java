package com.ai.travel.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AI 综合推荐请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendRequest {

    /**
     * 目的地
     */
    private String destination;

    /**
     * 行程天数
     */
    private Integer days;

    /**
     * 预算类型：经济型/舒适型/豪华型
     */
    private String budget;

    /**
     * 用户偏好标签（可选）
     */
    private List<String> interests;
}