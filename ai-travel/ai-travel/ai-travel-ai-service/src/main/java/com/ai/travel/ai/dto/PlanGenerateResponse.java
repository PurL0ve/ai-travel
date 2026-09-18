package com.ai.travel.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanGenerateResponse {

    private String destination;

    private Integer days;

    private String budget;

    private List<String> interests;

    private List<DailySchedule> dailySchedule;

    private Transportation transportation;

    private Accommodation accommodation;

    private Double estimatedCost;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailySchedule {
        private Integer day;
        private String morning;
        private String afternoon;
        private String evening;
        private String highlights;
        private String tips;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Transportation {
        private String toDestination;
        private String localTransport;
        private String tips;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Accommodation {
        private String recommendation;
        private String area;
        private String priceRange;
        private String bookingTips;
    }

}