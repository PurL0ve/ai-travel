package com.ai.travel.ai.feign;

import com.ai.travel.ai.dto.ProductInfoDTO;
import com.ai.travel.common.vo.PageResult;
import com.ai.travel.common.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "ai-travel-product-service")
public interface ProductFeignClient {

    @GetMapping("/api/product/list")
    Result<PageResult<ProductInfoDTO>> listProducts(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "destination", required = false) String destination,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    );
}