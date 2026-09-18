package com.ai.travel.order.feign;

import com.ai.travel.common.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class ProductFeignClientFallback implements ProductFeignClient {

    @Override
    public Result<ProductDetail> getProductDetail(Long productId) {
        log.warn("ProductService unavailable, using mock product data for productId={}", productId);
        // 开发环境下返回模拟数据，确保订单流程可用
        ProductDetail mockProduct = new ProductDetail(
                productId != null ? productId : 1L,
                "旅行产品 #" + productId,
                BigDecimal.valueOf(999.00),
                100,
                1 // 上架状态
        );
        return Result.success(mockProduct);
    }

    @Override
    public Result<Void> decreaseStock(Long productId, Integer quantity) {
        log.warn("ProductService unavailable, skipping stock decrease for productId={}", productId);
        return Result.success("库存扣减成功（模拟）");
    }

    @Override
    public Result<Void> increaseStock(Long productId, Integer quantity) {
        log.warn("ProductService unavailable, skipping stock increase for productId={}", productId);
        return Result.success("库存增加成功（模拟）");
    }
}