package com.ai.travel.order.service;

import com.ai.travel.common.vo.Result;
import com.ai.travel.order.dto.OrderCreateRequest;
import com.ai.travel.order.dto.OrderResponse;
import com.ai.travel.order.entity.Order;
import com.ai.travel.order.feign.ProductFeignClient;
import com.ai.travel.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductFeignClient productFeignClient;

    private static final AtomicLong sequence = new AtomicLong(0);

    @Transactional
    public Order createOrder(OrderCreateRequest request) {
        Result<ProductFeignClient.ProductDetail> productResult = 
                productFeignClient.getProductDetail(request.getProductId());
        
        if (productResult.getCode() != 200 || productResult.getData() == null) {
            throw new RuntimeException("产品不存在");
        }

        ProductFeignClient.ProductDetail product = productResult.getData();
        
        if (product.status() != 1) {
            throw new RuntimeException("产品已下架");
        }
        
        if (product.stock() < request.getQuantity()) {
            throw new RuntimeException("库存不足");
        }

        Result<Void> decreaseResult = productFeignClient.decreaseStock(
                request.getProductId(), request.getQuantity());
        
        if (decreaseResult.getCode() != 200) {
            throw new RuntimeException("扣减库存失败");
        }

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(request.getUserId());
        order.setProductId(request.getProductId());
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(product.price().multiply(BigDecimal.valueOf(request.getQuantity())));
        order.setStatus("待支付");
        order.setRemark(request.getRemark());

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully: orderNo={}", savedOrder.getOrderNo());
        return savedOrder;
    }

    @Transactional
    public Order payOrder(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!"待支付".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不允许支付");
        }

        order.setStatus("已支付");
        Order updatedOrder = orderRepository.save(order);
        log.info("Order paid successfully: orderNo={}", orderNo);
        return updatedOrder;
    }

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Order getOrderByOrderNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
    }

    @Transactional
    public Order cancelOrder(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        boolean wasPaid = "已支付".equals(order.getStatus());

        if (!"待支付".equals(order.getStatus()) && !"已支付".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不允许取消");
        }

        // 无论是否已支付，取消订单都需要把库存加回去
        Result<Void> increaseResult = productFeignClient.increaseStock(
                order.getProductId(), order.getQuantity());
        
        if (increaseResult.getCode() != 200) {
            log.warn("Failed to increase stock for order: {}", orderNo);
        }

        // 待支付 -> 已取消；已支付 -> 已退款
        order.setStatus(wasPaid ? "已退款" : "已取消");
        Order updatedOrder = orderRepository.save(order);
        log.info("Order cancelled successfully: orderNo={}, newStatus={}", orderNo, order.getStatus());
        return updatedOrder;
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        long seq = sequence.incrementAndGet() % 10000;
        return String.format("ORD%s%04d", timestamp, seq);
    }

}