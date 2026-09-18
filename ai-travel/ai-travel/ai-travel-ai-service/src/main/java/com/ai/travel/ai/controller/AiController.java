package com.ai.travel.ai.controller;

import com.ai.travel.ai.dto.PlanGenerateRequest;
import com.ai.travel.ai.dto.PlanGenerateResponse;
import com.ai.travel.ai.dto.ProductMatchRequest;
import com.ai.travel.ai.dto.ProductMatchResponse;
import com.ai.travel.ai.dto.RecommendRequest;
import com.ai.travel.ai.dto.RecommendResponse;
import com.ai.travel.ai.dto.SearchSimilarRequest;
import com.ai.travel.ai.dto.SearchSimilarResponse;
import com.ai.travel.ai.service.AiService;
import com.ai.travel.common.vo.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    /**
     * 🆕 综合推荐：多个 AI 行程方案 + 数据库产品匹配
     * 这是规划页面应该调用的接口
     */
    @PostMapping("/recommend")
    public Result<RecommendResponse> recommend(@RequestBody RecommendRequest request) {
        log.info("Recommend request: destination={}, days={}, budget={}",
                request.getDestination(), request.getDays(), request.getBudget());

        RecommendResponse response = aiService.recommend(request);
        return Result.success("推荐成功", response);
    }

    @PostMapping("/generate-plan")
    public Result<PlanGenerateResponse> generatePlan(@Valid @RequestBody PlanGenerateRequest request) {
        log.info("Generate plan request: destination={}, days={}, budget={}",
                request.getDestination(), request.getDays(), request.getBudget());

        PlanGenerateResponse response = aiService.generatePlan(request);
        return Result.success("行程生成成功", response);
    }

    @PostMapping("/search-similar")
    public Result<SearchSimilarResponse> searchSimilar(@Valid @RequestBody SearchSimilarRequest request) {
        log.info("Search similar request: query={}", request.getQuery());

        SearchSimilarResponse response = aiService.searchSimilar(request);
        return Result.success(response);
    }

    @PostMapping("/match-products")
    public Result<ProductMatchResponse> matchProducts(@RequestBody ProductMatchRequest request) {
        log.info("Match products request: destination={}, days={}, budget={}",
                request.getDestination(), request.getDays(), request.getBudget());

        ProductMatchResponse response = aiService.matchProducts(request);
        return Result.success("产品匹配成功", response);
    }

}