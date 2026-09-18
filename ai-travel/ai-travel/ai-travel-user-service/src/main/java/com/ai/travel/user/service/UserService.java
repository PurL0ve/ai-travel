package com.ai.travel.user.service;

import com.ai.travel.common.constant.SystemConstants;
import com.ai.travel.common.exception.BusinessException;
import com.ai.travel.user.dto.LoginRequest;
import com.ai.travel.user.dto.LoginResponse;
import com.ai.travel.user.dto.RegisterRequest;
import com.ai.travel.user.dto.UpdateRequest;
import com.ai.travel.user.dto.UserInfoResponse;
import com.ai.travel.user.entity.User;
import com.ai.travel.user.repository.UserRepository;
import com.ai.travel.user.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Tag(name = "用户服务", description = "用户注册、登录、信息管理")
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository userRepository,
                       @Lazy PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    @Operation(summary = "用户注册", description = "创建新用户账号")
    public User register(RegisterRequest request) {
        // 校验用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        // 校验邮箱是否已被注册
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException("邮箱已被注册");
            }
        }
        // 校验手机号是否已被注册
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            if (userRepository.existsByPhone(request.getPhone())) {
                throw new BusinessException("手机号已被注册");
            }
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(SystemConstants.DEFAULT_USER_ROLE);

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getUsername());
        return savedUser;
    }

    @Operation(summary = "用户登录", description = "用户登录并获取Token")
    public LoginResponse login(LoginRequest request) {
        User user;

        // 支持邮箱或用户名登录
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BusinessException("邮箱或密码错误"));
        } else if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new BusinessException("用户名或密码错误"));
        } else {
            throw new BusinessException("用户名或邮箱不能为空");
        }

        // 校验密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 生成Access Token（包含用户角色信息）
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        claims.put("userId", user.getId());
        String token = jwtTokenProvider.generateTokenWithClaims(user.getUsername(), claims);

        // 构建响应
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setNickname(user.getNickname());
        userInfo.setEmail(user.getEmail());
        userInfo.setPhone(user.getPhone());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setRole(user.getRole());

        LoginResponse response = new LoginResponse();
        response.setAccessToken(token);
        response.setTokenType(SystemConstants.TOKEN_PREFIX.trim());
        response.setExpiresIn(jwtTokenProvider.getExpireTime());
        response.setUser(userInfo);

        log.info("User logged in successfully: {}", user.getUsername());
        return response;
    }

    @Operation(summary = "获取用户信息", description = "根据用户名获取用户详细信息")
    public UserInfoResponse getUserInfo(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        UserInfoResponse response = new UserInfoResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setAvatar(user.getAvatar());
        response.setRole(user.getRole());

        return response;
    }

    @Transactional
    @Operation(summary = "更新用户信息", description = "更新用户个人信息")
    public UserInfoResponse updateUser(String username, UpdateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 更新昵称
        if (request.getNickname() != null && !request.getNickname().isEmpty()) {
            user.setNickname(request.getNickname());
        }

        // 更新邮箱（需校验唯一性）
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (!request.getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException("邮箱已被使用");
            }
            user.setEmail(request.getEmail());
        }

        // 更新手机号（需校验唯一性）
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            if (!request.getPhone().equals(user.getPhone()) &&
                userRepository.existsByPhone(request.getPhone())) {
                throw new BusinessException("手机号已被使用");
            }
            user.setPhone(request.getPhone());
        }

        // 更新头像
        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            user.setAvatar(request.getAvatar());
        }

        User updatedUser = userRepository.save(user);

        UserInfoResponse response = new UserInfoResponse();
        response.setId(updatedUser.getId());
        response.setUsername(updatedUser.getUsername());
        response.setNickname(updatedUser.getNickname());
        response.setEmail(updatedUser.getEmail());
        response.setPhone(updatedUser.getPhone());
        response.setAvatar(updatedUser.getAvatar());
        response.setRole(updatedUser.getRole());

        log.info("User updated successfully: {}", username);
        return response;
    }

    @Operation(summary = "根据用户名查询用户", description = "内部方法：根据用户名查找用户")
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Operation(summary = "修改用户密码", description = "修改当前登录用户的密码")
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 校验旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("旧密码不正确");
        }

        // 更新为新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", username);
    }

}