package com.ai.travel.product.entity;

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
 * 产品实体
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 产品名称
     */
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /**
     * 产品类型
     */
    @Column(name = "type", length = 50)
    private String type;

    /**
     * 目的地
     */
    @Column(name = "destination", length = 200)
    private String destination;

    /**
     * 行程天数
     */
    @Column(name = "days")
    private Integer days;

    /**
     * 价格
     */
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * 库存
     */
    @Column(name = "stock")
    private Integer stock;

    /**
     * 产品描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 包含项目
     */
    @Column(name = "includes", columnDefinition = "TEXT")
    private String includes;

    /**
     * 标签（逗号分隔）
     */
    @Column(name = "tags", length = 500)
    private String tags;

    /**
     * 状态：0-下架，1-上架
     */
    @Column(name = "status")
    private Integer status = SystemConstants.PRODUCT_STATUS_OFF;

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
     * 检查产品是否上架
     */
    public boolean isOnSale() {
        return SystemConstants.PRODUCT_STATUS_ON.equals(this.status);
    }

    /**
     * 检查库存是否充足
     */
    public boolean hasEnoughStock(int requiredQuantity) {
        return this.stock != null && this.stock >= requiredQuantity;
    }

    /**
     * 扣减库存
     */
    public void decreaseStock(int quantity) {
        if (!hasEnoughStock(quantity)) {
            throw new IllegalStateException("库存不足，当前库存：" + stock + "，需要：" + quantity);
        }
        this.stock -= quantity;
    }

}