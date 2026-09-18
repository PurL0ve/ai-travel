package com.ai.travel.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单状态更新请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateRequest {

    /**
     * 订单状态
     * PENDING-待支付, PAID-已支付, CANCELLED-已取消, COMPLETED-已完成
     */
    private String status;

}
