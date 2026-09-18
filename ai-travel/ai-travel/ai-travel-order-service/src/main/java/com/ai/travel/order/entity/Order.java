package com.ai.travel.order.entity;

import com.ai.travel.common.constant.SystemConstants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Entity
@Table(name = "orders", uniqueConstraints = {
    @UniqueConstraint(columnNames = "order_no")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 订单号（唯一）
     */
    @Column(name = "order_no", nullable = false, length = 50, unique = true)
    private String orderNo;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 产品ID
     */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /**
     * 购买数量
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * 总价
     */
    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;

    /**
     * 订单状态
     * PENDING-待支付, PAID-已支付, CANCELLED-已取消, COMPLETED-已完成
     */
    @Column(name = "status", length = 20)
    private String status = SystemConstants.ORDER_STATUS_PENDING;

    /**
     * 备注
     */
    @Column(name = "remark", length = 500)
    private String remark;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 检查订单是否可以取消
     */
    public boolean canBeCancelled() {
        return SystemConstants.ORDER_STATUS_PENDING.equals(this.status);
    }

    /**
     * 检查订单是否可以支付
     */
    public boolean canBePaid() {
        return SystemConstants.ORDER_STATUS_PENDING.equals(this.status);
    }

    /**
     * 检查订单是否已完成
     */
    public boolean isCompleted() {
        return SystemConstants.ORDER_STATUS_COMPLETED.equals(this.status);
    }

}