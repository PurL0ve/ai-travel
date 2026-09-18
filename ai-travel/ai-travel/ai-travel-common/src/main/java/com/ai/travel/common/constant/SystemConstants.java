package com.ai.travel.common.constant;

/**
 * 系统常量
 */
public class SystemConstants {

    /**
     * 默认用户角色
     */
    public static final String DEFAULT_USER_ROLE = "user";

    /**
     * 管理员角色
     */
    public static final String ADMIN_ROLE = "admin";

    /**
     * JWT认证头
     */
    public static final String AUTH_HEADER = "Authorization";

    /**
     * Token前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 分页默认页码
     */
    public static final int DEFAULT_PAGE_NUM = 1;

    /**
     * 分页默认每页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 分页最大每页大小
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * 预算类型 - 经济型
     */
    public static final String BUDGET_ECONOMY = "经济型";

    /**
     * 预算类型 - 舒适型
     */
    public static final String BUDGET_COMFORT = "舒适型";

    /**
     * 预算类型 - 豪华型
     */
    public static final String BUDGET_LUXURY = "豪华型";

    /**
     * 预算基础价格映射（元/天）
     */
    public static final java.util.Map<String, Double> BUDGET_BASE_COST = java.util.Map.of(
            BUDGET_ECONOMY, 500.0,
            BUDGET_COMFORT, 1000.0,
            BUDGET_LUXURY, 2000.0
    );

    /**
     * 产品状态 - 上架
     */
    public static final Integer PRODUCT_STATUS_ON = 1;

    /**
     * 产品状态 - 下架
     */
    public static final Integer PRODUCT_STATUS_OFF = 0;

    /**
     * 订单状态 - 待支付
     */
    public static final String ORDER_STATUS_PENDING = "待支付";

    /**
     * 订单状态 - 已支付
     */
    public static final String ORDER_STATUS_PAID = "已支付";

    /**
     * 订单状态 - 已取消
     */
    public static final String ORDER_STATUS_CANCELLED = "已取消";

    /**
     * 订单状态 - 已完成
     */
    public static final String ORDER_STATUS_COMPLETED = "已完成";

    /**
     * 订单状态 - 已退款
     */
    public static final String ORDER_STATUS_REFUNDED = "已退款";

    private SystemConstants() {
        // 防止实例化
    }
}