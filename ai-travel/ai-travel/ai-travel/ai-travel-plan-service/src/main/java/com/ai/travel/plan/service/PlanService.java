package com.ai.travel.plan.service;

import com.ai.travel.plan.dto.PlanCreateRequest;
import com.ai.travel.plan.dto.PlanResponse;
import com.ai.travel.plan.entity.Plan;
import com.ai.travel.plan.repository.PlanRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public Plan createPlan(PlanCreateRequest request) {
        Plan plan = new Plan();
        plan.setUserId(request.getUserId());
        plan.setDestination(request.getDestination());
        plan.setDays(request.getDays());
        plan.setBudgetType(request.getBudgetType());

        String title = generateTitle(request.getDestination(), request.getDays());
        plan.setTitle(title);

        PlanResponse.PlanDetail planDetail = generateMockPlanDetail(request);
        try {
            plan.setPlanJson(objectMapper.writeValueAsString(planDetail));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize plan detail", e);
            throw new RuntimeException("生成行程失败");
        }

        Plan savedPlan = planRepository.save(plan);
        log.info("Plan created successfully: userId={}, destination={}", request.getUserId(), request.getDestination());
        return savedPlan;
    }

    public List<Plan> getHistoryPlans(Long userId) {
        return planRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Plan getPlanById(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("行程不存在"));
    }

    @Transactional
    public void deletePlan(Long planId) {
        if (!planRepository.existsById(planId)) {
            throw new RuntimeException("行程不存在");
        }
        planRepository.deleteById(planId);
        log.info("Plan deleted successfully: id={}", planId);
    }

    private String generateTitle(String destination, Integer days) {
        return String.format("%s %d日游行程", destination, days);
    }

    private PlanResponse.PlanDetail generateMockPlanDetail(PlanCreateRequest request) {
        PlanResponse.PlanDetail detail = new PlanResponse.PlanDetail();
        
        StringBuilder schedule = new StringBuilder();
        for (int i = 1; i <= request.getDays(); i++) {
            schedule.append("Day ").append(i).append(": ");
            if (i == 1) {
                schedule.append("抵达目的地，入住酒店，市区自由活动");
            } else if (i == request.getDays()) {
                schedule.append("自由购物，返程");
            } else {
                schedule.append("景点游览（推荐景点：标志性建筑、当地特色景点）");
            }
            if (i < request.getDays()) {
                schedule.append("\\n");
            }
        }
        detail.setDailySchedule(schedule.toString());

        detail.setTransportation("建议选择飞机/高铁前往，当地可选择地铁、公交或打车出行");
        detail.setAccommodation(String.format("推荐入住%s酒店，根据预算选择合适档次", request.getDestination()));
        detail.setAttractions("当地著名景点包括：历史文化街区、自然风光景区、特色博物馆等");
        detail.setFoodRecommendations("推荐品尝当地特色美食，如：特色小吃、本地菜系");
        
        double baseCost = switch (request.getBudgetType()) {
            case "经济型" -> 500;
            case "舒适型" -> 1000;
            case "豪华型" -> 2000;
            default -> 1000;
        };
        detail.setEstimatedCost(baseCost * request.getDays());

        return detail;
    }

}