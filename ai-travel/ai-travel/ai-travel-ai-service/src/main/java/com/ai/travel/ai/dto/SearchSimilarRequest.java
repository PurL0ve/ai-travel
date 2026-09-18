package com.ai.travel.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchSimilarRequest {

    @NotBlank(message = "搜索文本不能为空")
    private String query;

    private Integer topK = 5;

    private String type;

}