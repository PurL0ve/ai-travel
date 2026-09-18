package com.ai.travel.plan.controller;

import com.ai.travel.common.vo.Result;
import com.ai.travel.plan.dto.PlanCreateRequest;
import com.ai.travel.plan.dto.PlanResponse;
import com.ai.travel.plan.entity.Plan;
import com.ai.travel.plan.service.PlanService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 行程控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/plan")
@RequiredArgsConstructor
@Tag(name = "行程管理", description = "行程创建、查询、删除接口")
public class PlanController {

    private final PlanService planService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "创建行程", description = "根据用户需求生成旅行行程")
    @PostMapping("/create")
    public Result<PlanResponse> createPlan(@Valid @RequestBody PlanCreateRequest request) {
        log.info("Create plan request: userId={}, destination={}, days={}, budgetType={}",
                request.getUserId(), request.getDestination(), request.getDays(), request.getBudgetType());

        Plan plan = planService.createPlan(request);
        return Result.success("行程创建成功", convertToResponse(plan));
    }

    @Operation(summary = "获取用户历史行程", description = "根据用户ID查询历史行程列表")
    @GetMapping("/history/{userId}")
    public Result<List<PlanResponse>> getHistoryPlans(
            @Parameter(description = "用户ID", required = true) @PathVariable("userId") Long userId) {
        log.info("Get history plans request: userId={}", userId);

        List<Plan> plans = planService.getHistoryPlans(userId);
        List<PlanResponse> responses = plans.stream()
                .map(this::convertToResponse)
                .toList();

        return Result.success(responses);
    }

    @Operation(summary = "获取行程详情", description = "根据行程ID查询详细信息")
    @GetMapping("/detail/{planId}")
    public Result<PlanResponse> getPlanDetail(
            @Parameter(description = "行程ID", required = true) @PathVariable("planId") Long planId) {
        log.info("Get plan detail request: planId={}", planId);

        Plan plan = planService.getPlanById(planId);
        return Result.success(convertToResponse(plan));
    }

    @Operation(summary = "删除行程", description = "根据行程ID删除行程")
    @DeleteMapping("/delete/{planId}")
    public Result<Void> deletePlan(
            @Parameter(description = "行程ID", required = true) @PathVariable("planId") Long planId) {
        log.info("Delete plan request: planId={}", planId);

        planService.deletePlan(planId);
        return Result.success("删除成功");
    }

    /**
     * 将Plan实体转换为PlanResponse DTO
     */
    private PlanResponse convertToResponse(Plan plan) {
        PlanResponse response = new PlanResponse();
        response.setId(plan.getId());
        response.setUserId(plan.getUserId());
        response.setTitle(plan.getTitle());
        response.setDestination(plan.getDestination());
        response.setDays(plan.getDays());
        response.setBudgetType(plan.getBudgetType());
        response.setCreatedAt(plan.getCreatedAt());
        if (plan.getPlanJson() != null) {
            try {
                PlanResponse.PlanDetail detail = objectMapper.readValue(
                        plan.getPlanJson(), PlanResponse.PlanDetail.class);
                response.setPlanDetail(detail);
            } catch (JsonProcessingException e) {
                log.error("Failed to deserialize plan detail", e);
            }
        }
        return response;
    }
}