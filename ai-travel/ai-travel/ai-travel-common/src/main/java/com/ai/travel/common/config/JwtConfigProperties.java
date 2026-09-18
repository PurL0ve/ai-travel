package com.ai.travel.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT配置属性
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfigProperties {

    /**
     * JWT签名密钥（从环境变量读取）
     */
    private String secret;

    /**
     * Token过期时间（毫秒），默认24小时
     */
    private Long expire = 86400000L;

    /**
     * 刷新Token过期时间（毫秒），默认7天
     */
    private Long refreshExpire = 604800000L;

    /**
     * Token前缀
     */
    private String tokenPrefix = "Bearer ";

    /**
     * 请求头名称
     */
    private String header = "Authorization";
}
