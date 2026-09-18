package com.ai.travel.user.entity;

import com.ai.travel.common.constant.SystemConstants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "username"),
    @UniqueConstraint(columnNames = "email"),
    @UniqueConstraint(columnNames = "phone")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * 用户ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户名（唯一）
     */
    @Column(name = "username", nullable = false, length = 50, unique = true)
    private String username;

    /**
     * 密码（BCrypt加密）
     */
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    /**
     * 昵称
     */
    @Column(name = "nickname", length = 50)
    private String nickname;

    /**
     * 邮箱（唯一）
     */
    @Column(name = "email", length = 100)
    private String email;

    /**
     * 手机号（唯一）
     */
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * 头像URL
     */
    @Column(name = "avatar", length = 500)
    private String avatar;

    /**
     * 角色：user-普通用户, admin-管理员
     */
    @Column(name = "role", length = 20)
    private String role = SystemConstants.DEFAULT_USER_ROLE;

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
     * 检查是否为管理员
     */
    public boolean isAdmin() {
        return SystemConstants.ADMIN_ROLE.equals(this.role);
    }

    /**
     * 检查是否为普通用户
     */
    public boolean isUser() {
        return SystemConstants.DEFAULT_USER_ROLE.equals(this.role);
    }

}