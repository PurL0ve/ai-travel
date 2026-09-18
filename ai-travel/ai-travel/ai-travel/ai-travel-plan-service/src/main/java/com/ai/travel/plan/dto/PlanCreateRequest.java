package com.ai.travel.plan.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanCreateRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "目的地不能为空")
    @Size(max = 200, message = "目的地长度不能超过200")
    private String destination;

    @NotNull(message = "天数不能为空")
    @Min(value = 1, message = "天数至少为1天")
    @Max(value = 30, message = "天数不能超过30天")
    private Integer days;

    @NotBlank(message = "预算类型不能为空")
    @Pattern(regexp = "^(经济型|舒适型|豪华型)$", message = "预算类型只能是：经济型、舒适型、豪华型")
    private String budgetType;

    private List<String> interests;

}