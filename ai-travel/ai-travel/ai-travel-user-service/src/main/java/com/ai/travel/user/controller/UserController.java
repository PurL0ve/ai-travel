package com.ai.travel.user.controller;

import com.ai.travel.common.vo.Result;
import com.ai.travel.user.dto.LoginRequest;
import com.ai.travel.user.dto.LoginResponse;
import com.ai.travel.user.dto.RegisterRequest;
import com.ai.travel.user.dto.UpdateRequest;
import com.ai.travel.user.dto.UserInfoResponse;
import com.ai.travel.user.entity.User;
import com.ai.travel.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户注册、登录、信息管理接口")
public class UserController {

    private final UserService userService;

    @Operation(summary = "用户注册", description = "创建新用户账号")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register request for username: {}", request.getUsername());
        userService.register(request);
        return Result.success("注册成功");
    }

    @Operation(summary = "用户登录", description = "用户登录并获取JWT Token")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for username: {}", request.getUsername());
        LoginResponse response = userService.login(request);
        return Result.success("登录成功", response);
    }

    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    @GetMapping("/info")
    public Result<UserInfoResponse> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Get user info for username: {}", username);
        UserInfoResponse response = userService.getUserInfo(username);
        return Result.success(response);
    }

    @Operation(summary = "更新用户信息", description = "更新当前登录用户的个人信息")
    @PutMapping("/update")
    public Result<UserInfoResponse> updateUser(@Valid @RequestBody UpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Update user request for username: {}", username);
        UserInfoResponse response = userService.updateUser(username, request);
        return Result.success("更新成功", response);
    }

}