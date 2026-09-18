package com.ai.travel.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanResponse {

    private Long id;

    private Long userId;

    private String title;

    private String destination;

    private Integer days;

    private String budgetType;

    private PlanDetail planDetail;

    private LocalDateTime createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanDetail {
        private String dailySchedule;
        private String transportation;
        private String accommodation;
        private String attractions;
        private String foodRecommendations;
        private Double estimatedCost;
    }

}