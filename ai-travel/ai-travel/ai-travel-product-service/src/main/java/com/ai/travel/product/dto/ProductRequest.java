package com.ai.travel.product.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "产品名称不能为空")
    @Size(max = 200, message = "产品名称长度不能超过200")
    private String name;

    @NotBlank(message = "产品类型不能为空")
    @Size(max = 50, message = "产品类型长度不能超过50")
    private String type;

    @NotBlank(message = "目的地不能为空")
    @Size(max = 200, message = "目的地长度不能超过200")
    private String destination;

    @NotNull(message = "行程天数不能为空")
    @Min(value = 1, message = "行程天数至少为1天")
    @Max(value = 365, message = "行程天数不能超过365天")
    private Integer days;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;

    @Size(max = 5000, message = "产品描述长度不能超过5000")
    private String description;

    @Size(max = 5000, message = "包含项目长度不能超过5000")
    private String includes;

    @Size(max = 500, message = "标签长度不能超过500")
    private String tags;

    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态只能为0或1")
    @Max(value = 1, message = "状态只能为0或1")
    private Integer status;

}