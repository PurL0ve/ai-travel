package com.ai.travel.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanGenerateRequest {

    @NotBlank(message = "目的地不能为空")
    private String destination;

    @NotNull(message = "天数不能为空")
    @Min(value = 1, message = "天数至少为1天")
    @Max(value = 30, message = "天数不能超过30天")
    private Integer days;

    @NotBlank(message = "预算类型不能为空")
    private String budget;

    private List<String> interests;

    private String companions;

}