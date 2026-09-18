package com.ai.travel.user.controller;

import com.ai.travel.common.vo.Result;
import com.ai.travel.user.dto.LoginRequest;
import com.ai.travel.user.dto.LoginResponse;
import com.ai.travel.user.dto.RegisterRequest;
import com.ai.travel.user.dto.UpdateRequest;
import com.ai.travel.user.dto.UserInfoResponse;
import com.ai.travel.user.entity.User;
import com.ai.travel.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register request for username: {}", request.getUsername());
        userService.register(request);
        return Result.success("注册成功");
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for username: {}", request.getUsername());
        LoginResponse response = userService.login(request);
        return Result.success("登录成功", response);
    }

    @GetMapping("/info")
    public Result<UserInfoResponse> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Get user info for username: {}", username);
        UserInfoResponse response = userService.getUserInfo(username);
        return Result.success(response);
    }

    @PutMapping("/update")
    public Result<UserInfoResponse> updateUser(@Valid @RequestBody UpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Update user request for username: {}", username);
        UserInfoResponse response = userService.updateUser(username, request);
        return Result.success("更新成功", response);
    }

}