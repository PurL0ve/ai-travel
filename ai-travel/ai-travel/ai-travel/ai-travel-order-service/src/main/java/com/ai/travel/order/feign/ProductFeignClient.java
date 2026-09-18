package com.ai.travel.order.feign;

import com.ai.travel.common.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "ai-travel-product-service", url = "http://localhost:8082", fallback = ProductFeignClientFallback.class)
public interface ProductFeignClient {

    @GetMapping("/api/product/detail/{id}")
    Result<ProductDetail> getProductDetail(@PathVariable("id") Long productId);

    @PutMapping("/api/product/stock/decrease")
    Result<Void> decreaseStock(@RequestParam("productId") Long productId, @RequestParam("quantity") Integer quantity);

    @PutMapping("/api/product/stock/increase")
    Result<Void> increaseStock(@RequestParam("productId") Long productId, @RequestParam("quantity") Integer quantity);

    record ProductDetail(
            Long id,
            String name,
            BigDecimal price,
            Integer stock,
            Integer status
    ) {}

}