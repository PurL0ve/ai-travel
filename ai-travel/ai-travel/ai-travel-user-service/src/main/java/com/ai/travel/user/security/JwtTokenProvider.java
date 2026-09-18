package com.ai.travel.user.security;

import com.ai.travel.common.config.JwtConfigProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Token提供者
 */
@Component
public class JwtTokenProvider {

    private final JwtConfigProperties jwtConfig;

    // 使用@Value注解兼容旧配置（会被JwtConfigProperties覆盖）
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expire}")
    private Long jwtExpire;

    public JwtTokenProvider(JwtConfigProperties jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    private SecretKey getSigningKey() {
        String secret = jwtConfig.getSecret() != null ? jwtConfig.getSecret() : jwtSecret;
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成Token（无额外claims）
     */
    public String generateToken(String username) {
        return generateTokenWithClaims(username, new HashMap<>());
    }

    /**
     * 生成Token（带自定义claims）
     */
    public String generateTokenWithClaims(String username, Map<String, Object> claims) {
        Long expireTime = jwtConfig.getExpire() != null ? jwtConfig.getExpire() : jwtExpire;
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 从Token中提取用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 从Token中提取过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 从Token中提取指定claim
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 解析Token获取所有claims
     */
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 验证Token是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 检查Token是否已过期
     */
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 获取Token过期时间（毫秒）
     */
    public Long getExpireTime() {
        return jwtConfig.getExpire() != null ? jwtConfig.getExpire() : jwtExpire;
    }

}