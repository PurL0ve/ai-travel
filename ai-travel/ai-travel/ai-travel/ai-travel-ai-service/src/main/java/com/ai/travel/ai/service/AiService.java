package com.ai.travel.ai.service;

import com.ai.travel.ai.dto.PlanGenerateRequest;
import com.ai.travel.ai.dto.PlanGenerateResponse;
import com.ai.travel.ai.dto.ProductInfoDTO;
import com.ai.travel.ai.dto.ProductMatchRequest;
import com.ai.travel.ai.dto.ProductMatchResponse;
import com.ai.travel.ai.dto.SearchSimilarRequest;
import com.ai.travel.ai.dto.SearchSimilarResponse;
import com.ai.travel.ai.feign.ProductFeignClient;
import com.ai.travel.common.vo.PageResult;
import com.ai.travel.common.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final Random random = new Random();
    private final ProductFeignClient productFeignClient;

    public PlanGenerateResponse generatePlan(PlanGenerateRequest request) {
        log.info("Generating plan for destination: {}, days: {}, budget: {}",
                request.getDestination(), request.getDays(), request.getBudget());

        PlanGenerateResponse response = new PlanGenerateResponse();
        response.setDestination(request.getDestination());
        response.setDays(request.getDays());
        response.setBudget(request.getBudget());
        response.setInterests(request.getInterests());

        List<PlanGenerateResponse.DailySchedule> dailySchedules = new ArrayList<>();
        for (int day = 1; day <= request.getDays(); day++) {
            dailySchedules.add(generateDailySchedule(request.getDestination(), day, request.getDays()));
        }
        response.setDailySchedule(dailySchedules);

        response.setTransportation(generateTransportation(request.getDestination()));
        response.setAccommodation(generateAccommodation(request.getDestination(), request.getBudget()));
        response.setEstimatedCost(estimateCost(request.getDays(), request.getBudget()));

        log.info("Plan generated successfully for destination: {}", request.getDestination());
        return response;
    }

    public SearchSimilarResponse searchSimilar(SearchSimilarRequest request) {
        log.info("Searching similar items for query: {}", request.getQuery());

        SearchSimilarResponse response = new SearchSimilarResponse();
        response.setQuery(request.getQuery());

        List<SearchSimilarResponse.SimilarItem> results = new ArrayList<>();

        String[] attractions = {"著名景点A", "历史文化街区", "自然风光景区", "特色博物馆", "美食街"};
        String[] descriptions = {
                "当地最著名的旅游景点，风景秀丽",
                "充满历史韵味的老街区",
                "自然风光优美，适合拍照",
                "展示当地历史文化的重要场所",
                "汇聚各种特色美食"
        };
        String[] locations = {"市中心", "郊区", "海边", "山区", "古镇"};

        int count = Math.min(request.getTopK(), 5);
        for (int i = 0; i < count; i++) {
            SearchSimilarResponse.SimilarItem item = new SearchSimilarResponse.SimilarItem();
            item.setId((long) (i + 1));
            item.setName(attractions[i]);
            item.setType(request.getType() != null ? request.getType() : "attraction");
            item.setDescription(descriptions[i]);
            item.setLocation(locations[i]);
            item.setScore(Math.round((0.7 + random.nextDouble() * 0.3) * 100.0) / 100.0);
            item.setImageUrl("https://example.com/image" + (i + 1) + ".jpg");
            results.add(item);
        }

        response.setResults(results);
        return response;
    }

    public ProductMatchResponse matchProducts(ProductMatchRequest request) {
        log.info("Matching products for destination: {}, days: {}, budget: {}",
                request.getDestination(), request.getDays(), request.getBudget());

        ProductMatchResponse response = new ProductMatchResponse();
        response.setDestination(request.getDestination());
        response.setDays(request.getDays());

        List<ProductMatchResponse.MatchedProduct> products = new ArrayList<>();

        try {
            Result<PageResult<ProductInfoDTO>> result = productFeignClient.listProducts(
                    null, request.getDestination(), null, null, 1, 20
            );

            if (result != null && result.getData() != null && result.getData().getList() != null) {
                for (ProductInfoDTO p : result.getData().getList()) {
                    ProductMatchResponse.MatchedProduct matched = new ProductMatchResponse.MatchedProduct();
                    matched.setProductId(p.getId());
                    matched.setProductName(p.getName());
                    matched.setType(p.getType());
                    matched.setPrice(p.getPrice());
                    matched.setDestination(p.getDestination());
                    matched.setDays(p.getDays());
                    matched.setTags(p.getTags());
                    matched.setDescription(p.getDescription());
                    matched.setMatchScore(Math.round((0.6 + random.nextDouble() * 0.4) * 100.0) / 100.0);
                    products.add(matched);
                }
            } else {
                log.warn("Product service returned empty result for destination: {}", request.getDestination());
            }
        } catch (Exception e) {
            log.error("Failed to fetch products from product-service for destination: {}", request.getDestination(), e);
        }

        products.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));
        response.setProducts(products);
        return response;
    }

    private PlanGenerateResponse.DailySchedule generateDailySchedule(String destination, int day, int totalDays) {
        PlanGenerateResponse.DailySchedule schedule = new PlanGenerateResponse.DailySchedule();
        schedule.setDay(day);

        if (day == 1) {
            schedule.setMorning("抵达" + destination + "，办理酒店入住");
            schedule.setAfternoon("市区观光，熟悉周边环境");
            schedule.setEvening("品尝当地特色美食");
            schedule.setHighlights("欢迎晚宴");
            schedule.setTips("建议提前兑换当地货币");
        } else if (day == totalDays) {
            schedule.setMorning("自由活动，购物");
            schedule.setAfternoon("收拾行李，办理退房");
            schedule.setEvening("返程");
            schedule.setHighlights("返程准备");
            schedule.setTips("注意返程时间");
        } else {
            schedule.setMorning("景点A游览");
            schedule.setAfternoon("景点B游览");
            schedule.setEvening("特色餐厅用餐");
            schedule.setHighlights("当地特色体验");
            schedule.setTips("建议提前预约热门景点");
        }

        return schedule;
    }

    private PlanGenerateResponse.Transportation generateTransportation(String destination) {
        PlanGenerateResponse.Transportation transportation = new PlanGenerateResponse.Transportation();
        transportation.setToDestination("建议选择飞机或高铁前往");
        transportation.setLocalTransport("地铁、公交、出租车");
        transportation.setTips("下载当地交通APP方便出行");
        return transportation;
    }

    private PlanGenerateResponse.Accommodation generateAccommodation(String destination, String budget) {
        PlanGenerateResponse.Accommodation accommodation = new PlanGenerateResponse.Accommodation();
        accommodation.setRecommendation(getHotelRecommendation(budget));
        accommodation.setArea("市中心或景区附近");
        accommodation.setPriceRange(getPriceRange(budget));
        accommodation.setBookingTips("建议提前1-2周预订");
        return accommodation;
    }

    private String getHotelRecommendation(String budget) {
        return switch (budget) {
            case "经济型" -> "连锁酒店或民宿";
            case "舒适型" -> "三星级或精品酒店";
            case "豪华型" -> "五星级酒店或度假村";
            default -> "品质酒店";
        };
    }

    private String getPriceRange(String budget) {
        return switch (budget) {
            case "经济型" -> "200-400元/晚";
            case "舒适型" -> "400-800元/晚";
            case "豪华型" -> "800元以上/晚";
            default -> "400-800元/晚";
        };
    }

    private Double estimateCost(Integer days, String budget) {
        double dailyCost = switch (budget) {
            case "经济型" -> 500;
            case "舒适型" -> 1000;
            case "豪华型" -> 2000;
            default -> 1000;
        };
        return dailyCost * days;
    }

}