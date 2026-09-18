package com.ai.travel.order.feign;

import com.ai.travel.common.vo.Result;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductFeignClientFallback implements ProductFeignClient {

    @Override
    public Result<ProductDetail> getProductDetail(Long productId) {
        return Result.error("商品服务暂不可用");
    }

    @Override
    public Result<Void> decreaseStock(Long productId, Integer quantity) {
        return Result.error("商品服务暂不可用");
    }

    @Override
    public Result<Void> increaseStock(Long productId, Integer quantity) {
        return Result.error("商品服务暂不可用");
    }
}