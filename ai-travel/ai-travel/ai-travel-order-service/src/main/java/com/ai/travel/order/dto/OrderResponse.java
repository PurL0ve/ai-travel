package com.ai.travel.order.dto;

import com.ai.travel.common.constant.SystemConstants;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     * 总价
     */
    private BigDecimal totalPrice;

    /**
     * 订单状态
     * PENDING-待支付, PAID-已支付, CANCELLED-已取消, COMPLETED-已完成
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 获取状态中文描述
     */
    @Operation(hidden = true)
    public String getStatusDescription() {
        return switch (status) {
            case SystemConstants.ORDER_STATUS_PENDING -> "待支付";
            case SystemConstants.ORDER_STATUS_PAID -> "已支付";
            case SystemConstants.ORDER_STATUS_CANCELLED -> "已取消";
            case SystemConstants.ORDER_STATUS_COMPLETED -> "已完成";
            default -> "未知状态";
        };
    }

}