package com.ai.travel.ai.service;

import com.ai.travel.ai.dto.PlanGenerateRequest;
import com.ai.travel.ai.dto.PlanGenerateResponse;
import com.ai.travel.ai.dto.ProductInfoDTO;
import com.ai.travel.ai.dto.ProductMatchRequest;
import com.ai.travel.ai.dto.ProductMatchResponse;
import com.ai.travel.ai.dto.RecommendRequest;
import com.ai.travel.ai.dto.RecommendResponse;
import com.ai.travel.ai.dto.SearchSimilarRequest;
import com.ai.travel.ai.dto.SearchSimilarResponse;
import com.ai.travel.ai.feign.ProductFeignClient;
import com.ai.travel.common.vo.PageResult;
import com.ai.travel.common.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    /**
     * 综合推荐：生成多个 AI 行程方案 + 匹配数据库中的旅游产品
     */
    public RecommendResponse recommend(RecommendRequest request) {
        String dest = request.getDestination();
        int days = request.getDays() != null ? request.getDays() : 3;
        String budget = request.getBudget() != null ? request.getBudget() : "舒适型";

        log.info("Generating recommendations for destination={}, days={}, budget={}", dest, days, budget);

        RecommendResponse response = new RecommendResponse();
        response.setDestination(dest);
        response.setDays(days);
        response.setBudget(budget);

        // 1. 生成多个不同主题的行程方案
        List<RecommendResponse.PlanOption> planOptions = generatePlanOptions(dest, days, budget);
        response.setPlans(planOptions);

        // 2. 从数据库查询匹配的旅游产品
        List<RecommendResponse.MatchedProduct> matchedProducts = fetchMatchedProducts(dest, days, budget);
        response.setProducts(matchedProducts);

        return response;
    }

    /**
     * 生成 3 个不同主题的行程方案
     */
    private List<RecommendResponse.PlanOption> generatePlanOptions(String dest, int days, String budget) {
        List<RecommendResponse.PlanOption> options = new ArrayList<>();

        // 方案一：文化深度游
        options.add(buildPlanOption(dest, days, budget, "文化深度游",
                "深入了解" + dest + "的历史文化，参观博物馆、古迹、传统街区",
                new String[]{"博物馆", "历史古迹", "传统街区", "文化遗址", "非遗体验"},
                new String[]{"本地特色菜", "老字号餐厅", "文化主题餐厅"},
                new String[]{"导游讲解", "文化体验课程", "手工艺制作"}));

        // 方案二：美食休闲游
        options.add(buildPlanOption(dest, days, budget, "美食休闲游",
                "尝遍" + dest + "地道美食，享受悠闲的度假时光",
                new String[]{"美食街", "网红打卡地", "特色市集", "城市公园", "商业街区"},
                new String[]{"地道小吃", "网红餐厅", "本地私房菜", "夜市大排档"},
                new String[]{"美食探店", "下午茶时光", "SPA放松"}));

        // 方案三：自然探险游
        options.add(buildPlanOption(dest, days, budget, "自然探险游",
                "探索" + dest + "的自然风光，体验户外运动的乐趣",
                new String[]{"自然风景区", "登山徒步", "湖泊河流", "森林公园", "户外营地"},
                new String[]{"农家菜", "野餐烧烤", "山珍野味", "本地特色"},
                new String[]{"徒步登山", "摄影打卡", "日出日落观赏"}));

        return options;
    }

    private RecommendResponse.PlanOption buildPlanOption(String dest, int days, String budget,
                                                         String theme, String desc,
                                                         String[] attractionTypes, String[] foodTypes, String[] extras) {
        RecommendResponse.PlanOption option = new RecommendResponse.PlanOption();
        option.setTheme(theme);
        option.setDescription(desc);

        List<RecommendResponse.DayItem> schedule = new ArrayList<>();
        for (int d = 1; d <= days; d++) {
            RecommendResponse.DayItem item = new RecommendResponse.DayItem();
            item.setDay(d);
            if (d == 1) {
                item.setMorning("抵达" + dest + "，办理酒店入住");
                item.setAfternoon("稍作休息后，前往" + attractionTypes[0] + "初步游览");
                item.setEvening("品尝" + foodTypes[0] + "，感受" + dest + "的烟火气");
                item.setHighlight("初识" + dest);
                item.setTip("提前下载离线地图，了解当地交通");
            } else if (d == days) {
                item.setMorning("前往" + attractionTypes[Math.min(d - 2, attractionTypes.length - 1)] + "，购买纪念品");
                item.setAfternoon("收拾行李，办理退房");
                item.setEvening("带着美好回忆返程");
                item.setHighlight("满载而归");
                item.setTip("提前2小时到达机场/车站");
            } else {
                int idx = (d - 2) % attractionTypes.length;
                item.setMorning("游览" + attractionTypes[idx] + "，建议停留2-3小时");
                item.setAfternoon("体验" + (extras.length > idx ? extras[idx] : "当地特色"));
                item.setEvening("品尝" + foodTypes[idx % foodTypes.length] + "，漫步" + dest + "街头");
                item.setHighlight(attractionTypes[idx] + "深度体验");
                item.setTip("穿舒适的鞋子，做好防晒");
            }
            schedule.add(item);
        }
        option.setSchedule(schedule);

        // 预估费用
        double dailyCost = switch (budget) {
            case "经济型" -> 500;
            case "舒适型" -> 1000;
            case "豪华型" -> 2000;
            default -> 1000;
        };
        option.setEstimatedCost(dailyCost * days);

        return option;
    }

    /**
     * 从产品数据库查询匹配的旅游产品
     */
    private List<RecommendResponse.MatchedProduct> fetchMatchedProducts(String dest, int days, String budget) {
        List<RecommendResponse.MatchedProduct> matched = new ArrayList<>();
        try {
            Result<PageResult<ProductInfoDTO>> result = productFeignClient.listProducts(
                    null, dest, null, null, 1, 10
            );
            if (result != null && result.getData() != null && result.getData().getList() != null) {
                for (ProductInfoDTO p : result.getData().getList()) {
                    RecommendResponse.MatchedProduct mp = new RecommendResponse.MatchedProduct();
                    mp.setProductId(p.getId());
                    mp.setProductName(p.getName());
                    mp.setType(p.getType());
                    mp.setDestination(p.getDestination());
                    mp.setDays(p.getDays());
                    mp.setPrice(p.getPrice());
                    mp.setTags(p.getTags());
                    mp.setDescription(p.getDescription());
                    // 匹配度评分
                    double score = 0.85;
                    if (p.getDays() != null && p.getDays().equals(days)) score += 0.1;
                    if (dest != null && p.getDestination() != null && p.getDestination().contains(dest)) score += 0.05;
                    mp.setMatchScore(Math.min(score, 1.0));
                    matched.add(mp);
                }
            } else {
                log.info("No products found in DB for destination={}, using mock data", dest);
                matched.addAll(createMockProducts(dest, days, budget));
            }
        } catch (Exception e) {
            log.warn("Failed to fetch products from DB, using mock data: {}", e.getMessage());
            matched.addAll(createMockProducts(dest, days, budget));
        }
        matched.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));
        return matched;
    }

    /**
     * 当数据库无数据时生成模拟产品
     */
    private List<RecommendResponse.MatchedProduct> createMockProducts(String dest, int days, String budget) {
        List<RecommendResponse.MatchedProduct> list = new ArrayList<>();

        double basePrice = switch (budget) {
            case "经济型" -> 500.0;
            case "舒适型" -> 1200.0;
            case "豪华型" -> 2500.0;
            default -> 1200.0;
        };

        String[][] mockData = {
            {dest + "经典" + days + "日游", "跟团游", days + "天" + (days - 1) + "晚全覆盖，含往返交通+住宿+门票", "经典线路;高性价比"},
            {dest + "深度" + days + "日自由行", "自由行", "机酒套餐，自由安排行程，含" + (days - 1) + "晚品质住宿", "自由行;灵活;深度"},
            {dest + "精品小团" + days + "日", "小团游", "6人精品小团，专车接送，精选景点不赶路", "小团;精品;舒适"},
        };

        for (int i = 0; i < mockData.length; i++) {
            RecommendResponse.MatchedProduct mp = new RecommendResponse.MatchedProduct();
            mp.setProductId((long) (i + 1));
            mp.setProductName(mockData[i][0]);
            mp.setType(mockData[i][1]);
            mp.setDestination(dest);
            mp.setDays(days);
            mp.setPrice(BigDecimal.valueOf(basePrice * (i + 1)));
            mp.setDescription(mockData[i][2]);
            mp.setTags(mockData[i][3]);
            mp.setMatchScore(0.9 - i * 0.1);
            list.add(mp);
        }
        return list;
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