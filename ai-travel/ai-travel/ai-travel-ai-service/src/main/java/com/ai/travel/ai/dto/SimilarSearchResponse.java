package com.ai.travel.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimilarSearchResponse {

    private String query;

    private List<SimilarItem> results;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimilarItem {
        private Long id;
        private String name;
        private String type;
        private String description;
        private String location;
        private Double score;
    }

}