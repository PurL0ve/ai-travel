package com.ai.travel.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductMatchRequest {

    private String destination;

    private Integer days;

    private String budget;

    private List<String> interests;

    private List<String> keywords;

}