package com.ai.travel.order.controller;

import com.ai.travel.common.vo.Result;
import com.ai.travel.order.dto.OrderCreateRequest;
import com.ai.travel.order.dto.OrderResponse;
import com.ai.travel.order.entity.Order;
import com.ai.travel.order.feign.ProductFeignClient;
import com.ai.travel.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final ProductFeignClient productFeignClient;

    @PostMapping("/create")
    public Result<OrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        log.info("Create order request: userId={}, productId={}, quantity={}",
                request.getUserId(), request.getProductId(), request.getQuantity());

        Order order = orderService.createOrder(request);
        return Result.success("订单创建成功", convertToResponse(order));
    }

    @PutMapping("/pay/{orderNo}")
    public Result<OrderResponse> payOrder(@PathVariable("orderNo") String orderNo) {
        log.info("Pay order request: orderNo={}", orderNo);

        Order order = orderService.payOrder(orderNo);
        return Result.success("支付成功", convertToResponse(order));
    }

    @GetMapping("/list/{userId}")
    public Result<List<OrderResponse>> getUserOrders(@PathVariable("userId") Long userId) {
        log.info("Get user orders request: userId={}", userId);

        List<Order> orders = orderService.getUserOrders(userId);
        List<OrderResponse> responses = orders.stream()
                .map(this::convertToResponse)
                .toList();

        return Result.success(responses);
    }

    @GetMapping("/detail/{orderNo}")
    public Result<OrderResponse> getOrderDetail(@PathVariable("orderNo") String orderNo) {
        log.info("Get order detail request: orderNo={}", orderNo);

        Order order = orderService.getOrderByOrderNo(orderNo);
        return Result.success(convertToResponse(order));
    }

    @PutMapping("/cancel/{orderNo}")
    public Result<OrderResponse> cancelOrder(@PathVariable("orderNo") String orderNo) {
        log.info("Cancel order request: orderNo={}", orderNo);

        Order order = orderService.cancelOrder(orderNo);
        return Result.success("订单已取消", convertToResponse(order));
    }

    private OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNo(order.getOrderNo());
        response.setUserId(order.getUserId());
        response.setProductId(order.getProductId());
        response.setQuantity(order.getQuantity());
        response.setTotalPrice(order.getTotalPrice());
        response.setStatus(order.getStatus());
        response.setRemark(order.getRemark());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        try {
            Result<ProductFeignClient.ProductDetail> productResult =
                    productFeignClient.getProductDetail(order.getProductId());
            if (productResult.getCode() == 200 && productResult.getData() != null) {
                response.setProductName(productResult.getData().name());
            }
        } catch (Exception e) {
            log.warn("Failed to get product name for order: {}", order.getOrderNo());
        }
        return response;
    }
}