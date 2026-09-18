package com.ai.travel.plan.controller;

import com.ai.travel.common.vo.Result;
import com.ai.travel.plan.dto.PlanCreateRequest;
import com.ai.travel.plan.dto.PlanResponse;
import com.ai.travel.plan.entity.Plan;
import com.ai.travel.plan.service.PlanService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/plan")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;
    private final ObjectMapper objectMapper;

    @PostMapping("/create")
    public Result<PlanResponse> createPlan(@Valid @RequestBody PlanCreateRequest request) {
        log.info("Create plan request: userId={}, destination={}, days={}, budgetType={}",
                request.getUserId(), request.getDestination(), request.getDays(), request.getBudgetType());

        Plan plan = planService.createPlan(request);
        return Result.success("行程创建成功", convertToResponse(plan));
    }

    @GetMapping("/history/{userId}")
    public Result<List<PlanResponse>> getHistoryPlans(@PathVariable("userId") Long userId) {
        log.info("Get history plans request: userId={}", userId);

        List<Plan> plans = planService.getHistoryPlans(userId);
        List<PlanResponse> responses = plans.stream()
                .map(this::convertToResponse)
                .toList();

        return Result.success(responses);
    }

    @GetMapping("/detail/{planId}")
    public Result<PlanResponse> getPlanDetail(@PathVariable("planId") Long planId) {
        log.info("Get plan detail request: planId={}", planId);

        Plan plan = planService.getPlanById(planId);
        return Result.success(convertToResponse(plan));
    }

    @DeleteMapping("/delete/{planId}")
    public Result<Void> deletePlan(@PathVariable("planId") Long planId) {
        log.info("Delete plan request: planId={}", planId);

        planService.deletePlan(planId);
        return Result.success("删除成功");
    }

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