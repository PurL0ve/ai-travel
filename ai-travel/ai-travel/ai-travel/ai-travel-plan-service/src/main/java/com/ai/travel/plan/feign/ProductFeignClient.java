package com.ai.travel.plan.feign;

import com.ai.travel.common.vo.PageResult;
import com.ai.travel.common.vo.Result;
import com.ai.travel.plan.dto.ProductInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "ai-travel-product-service")
public interface ProductFeignClient {

    @GetMapping("/api/product/list")
    Result<PageResult<ProductInfoDTO>> listProducts(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    );
}