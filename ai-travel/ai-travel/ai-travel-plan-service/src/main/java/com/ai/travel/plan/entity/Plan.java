package com.ai.travel.plan.entity;

import com.ai.travel.common.constant.SystemConstants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 旅行计划实体
 */
@Entity
@Table(name = "plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 行程标题
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 目的地
     */
    @Column(name = "destination", length = 200)
    private String destination;

    /**
     * 行程天数
     */
    @Column(name = "days")
    private Integer days;

    /**
     * 预算类型：经济型/舒适型/豪华型
     */
    @Column(name = "budget_type", length = 20)
    private String budgetType;

    /**
     * 行程详情（JSON格式）
     * 包含：每日安排、交通建议、住宿推荐、景点推荐、美食推荐、预算估算
     */
    @Column(name = "plan_json", columnDefinition = "JSON")
    private String planJson;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 获取预算类型中文描述
     */
    public String getBudgetTypeDescription() {
        return switch (budgetType) {
            case SystemConstants.BUDGET_ECONOMY -> "经济型";
            case SystemConstants.BUDGET_COMFORT -> "舒适型";
            case SystemConstants.BUDGET_LUXURY -> "豪华型";
            default -> budgetType != null ? budgetType : "未指定";
        };
    }

}