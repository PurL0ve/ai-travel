package com.ai.travel.plan.service;

import com.ai.travel.common.constant.SystemConstants;
import com.ai.travel.common.exception.BusinessException;
import com.ai.travel.plan.dto.PlanCreateRequest;
import com.ai.travel.plan.dto.PlanResponse;
import com.ai.travel.plan.entity.Plan;
import com.ai.travel.plan.repository.PlanRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 行程服务
 */
@Slf4j
@Service
@Tag(name = "行程服务", description = "行程创建、查询、删除")
public class PlanService {

    private final PlanRepository planRepository;
    private final ObjectMapper objectMapper;

    public PlanService(PlanRepository planRepository, ObjectMapper objectMapper) {
        this.planRepository = planRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    @Operation(summary = "创建行程", description = "根据用户需求生成旅行行程")
    public Plan createPlan(PlanCreateRequest request) {
        // 参数校验
        if (request.getDays() == null || request.getDays() < 1) {
            throw new BusinessException("行程天数必须大于0");
        }
        if (request.getDestination() == null || request.getDestination().isEmpty()) {
            throw new BusinessException("目的地不能为空");
        }

        Plan plan = new Plan();
        plan.setUserId(request.getUserId());
        plan.setDestination(request.getDestination());
        plan.setDays(request.getDays());
        plan.setBudgetType(request.getBudgetType());

        // 生成标题
        String title = generateTitle(request.getDestination(), request.getDays());
        plan.setTitle(title);

        // 生成行程详情
        PlanResponse.PlanDetail planDetail = generateMockPlanDetail(request);
        try {
            plan.setPlanJson(objectMapper.writeValueAsString(planDetail));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize plan detail", e);
            throw new BusinessException("生成行程失败");
        }

        Plan savedPlan = planRepository.save(plan);
        log.info("Plan created successfully: userId={}, destination={}, days={}",
                request.getUserId(), request.getDestination(), request.getDays());
        return savedPlan;
    }

    @Operation(summary = "获取用户历史行程", description = "根据用户ID查询历史行程列表")
    public List<Plan> getHistoryPlans(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        return planRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Operation(summary = "获取行程详情", description = "根据行程ID查询详细信息")
    public Plan getPlanById(Long planId) {
        if (planId == null) {
            throw new BusinessException("行程ID不能为空");
        }
        return planRepository.findById(planId)
                .orElseThrow(() -> new BusinessException("行程不存在"));
    }

    @Transactional
    @Operation(summary = "删除行程", description = "根据行程ID删除行程")
    public void deletePlan(Long planId) {
        if (planId == null) {
            throw new BusinessException("行程ID不能为空");
        }
        if (!planRepository.existsById(planId)) {
            throw new BusinessException("行程不存在");
        }
        planRepository.deleteById(planId);
        log.info("Plan deleted successfully: id={}", planId);
    }

    /**
     * 生成行程标题
     */
    private String generateTitle(String destination, Integer days) {
        return String.format("%s %d日游行程", destination, days);
    }

    /**
     * 生成智能行程详情（模拟AI生成）
     */
    private PlanResponse.PlanDetail generateMockPlanDetail(PlanCreateRequest request) {
        String dest = request.getDestination();
        int days = request.getDays();
        String budget = request.getBudgetType();
        PlanResponse.PlanDetail detail = new PlanResponse.PlanDetail();

        // 目的地特色数据
        final DestInfo info = getDestInfo(dest);

        // 生成每日详细行程
        java.util.List<PlanResponse.DayPlan> dailySchedules = new java.util.ArrayList<>();
        for (int i = 1; i <= days; i++) {
            PlanResponse.DayPlan dayPlan = new PlanResponse.DayPlan();
            dayPlan.setDay(i);
            if (i == 1) {
                dayPlan.setMorning("抵达" + dest + "，办理" + getHotelLevel(budget) + "入住");
                dayPlan.setAfternoon("在酒店周边漫步，适应环境，参观附近的" + info.landmark);
                dayPlan.setEvening("品尝" + dest + "特色美食——" + info.dinnerFood);
                dayPlan.setHighlights("初识" + dest + "，感受当地风情");
                dayPlan.setTips("建议提前下载离线地图");
            } else if (i == days) {
                dayPlan.setMorning("前往" + info.shoppingArea + "购买纪念品和特产");
                dayPlan.setAfternoon("收拾行李，办理退房，前往机场/车站");
                dayPlan.setEvening("返程，结束愉快的" + dest + "之旅");
                dayPlan.setHighlights("满载而归，回忆满满");
                dayPlan.setTips("提前2小时到达机场/车站");
            } else {
                String attraction = info.attractions[(i - 2) % info.attractions.length];
                String snack = info.snacks[(i - 2) % info.snacks.length];
                dayPlan.setMorning("游览" + attraction + "，建议停留2-3小时");
                dayPlan.setAfternoon("漫步" + info.district + "街区，体验当地人文气息");
                dayPlan.setEvening("品尝" + snack + "等地道小吃，欣赏" + dest + "夜景");
                dayPlan.setHighlights(attraction + "深度游览");
                dayPlan.setTips("建议穿舒适的鞋子，带好相机");
            }
            dailySchedules.add(dayPlan);
        }
        detail.setDailySchedules(dailySchedules);

        // 交通建议
        detail.setTransportation(info.transportTip);

        // 住宿建议
        detail.setAccommodation(String.format("推荐入住%s的%s，%s",
                dest, info.accommodationArea, getHotelAdvice(budget)));

        // 景点推荐
        detail.setAttractions(String.join("、", info.attractions) + "、" + info.landmark);

        // 美食推荐
        detail.setFoodRecommendations(info.foodieAdvice);

        // 预算计算
        Double baseCost = SystemConstants.BUDGET_BASE_COST.get(budget);
        if (baseCost == null) {
            baseCost = 1000.0;
            log.warn("Unknown budget type: {}, using default cost", budget);
        }
        detail.setEstimatedCost(baseCost * days);

        return detail;
    }

    private String getHotelLevel(String budget) {
        return switch (budget) {
            case "经济型" -> "经济连锁酒店";
            case "舒适型" -> "精品商务酒店";
            case "豪华型" -> "五星级度假酒店";
            default -> "品质酒店";
        };
    }

    private String getHotelAdvice(String budget) {
        return switch (budget) {
            case "经济型" -> "预算约200-400元/晚";
            case "舒适型" -> "预算约400-800元/晚";
            case "豪华型" -> "预算约800-1500元/晚";
            default -> "预算约400-800元/晚";
        };
    }

    /**
     * 目的地特色信息
     */
    private DestInfo getDestInfo(String dest) {
        if (dest != null) {
            if (dest.contains("北京")) return new DestInfo(
                new String[]{"故宫博物院", "八达岭长城", "颐和园", "天坛公园", "798艺术区"},
                "前门大栅栏", "天安门广场", "南锣鼓巷", "王府井步行街",
                "地铁出行最便捷，下载亿通行APP扫码乘车。机场有首都机场线直达市区。",
                "市中心（王府井/前门/西单区域）",
                "北京烤鸭、炸酱面、涮羊肉、豆汁焦圈、驴打滚、糖葫芦",
                "全聚德烤鸭、老北京炸酱面、东来顺涮肉、门框卤煮"
            );
            if (dest.contains("上海")) return new DestInfo(
                new String[]{"外滩万国建筑群", "上海迪士尼乐园", "豫园城隍庙", "上海博物馆", "田子坊"},
                "南京路步行街", "东方明珠塔", "新天地时尚街区", "陆家嘴商圈",
                "地铁网络发达，下载Metro大都会APP。浦东/虹桥两大机场可选。",
                "外滩/南京路/人民广场区域",
                "小笼包、生煎馒头、蟹壳黄、葱油拌面、红烧肉、白斩鸡",
                "南翔馒头店、小杨生煎、德兴馆、老正兴"
            );
            if (dest.contains("杭州")) return new DestInfo(
                new String[]{"西湖十景", "灵隐寺飞来峰", "西溪湿地", "宋城千古情", "龙井茶园"},
                "河坊街", "雷峰塔", "南山路文艺街", "武林广场",
                "杭州地铁覆盖主要景区，推荐骑共享单车游西湖。萧山国际机场航班便利。",
                "西湖周边（湖滨/南山路/北山街区域）",
                "西湖醋鱼、龙井虾仁、东坡肉、叫花鸡、片儿川、葱包桧",
                "楼外楼、知味观、奎元馆、外婆家"
            );
            if (dest.contains("成都")) return new DestInfo(
                new String[]{"大熊猫繁育基地", "宽窄巷子", "锦里古街", "武侯祠", "都江堰"},
                "春熙路太古里", "IFS熊猫爬墙", "九眼桥酒吧街", "建设路小吃街",
                "成都地铁四通八达，下载天府通APP。双流/天府两大机场可选。",
                "春熙路/太古里/宽窄巷子周边",
                "火锅、串串香、担担面、龙抄手、夫妻肺片、兔头",
                "小龙坎火锅、蜀大侠、龙抄手总店、陈麻婆豆腐"
            );
            if (dest.contains("三亚")) return new DestInfo(
                new String[]{"亚龙湾天堂森林公园", "蜈支洲岛", "天涯海角", "南山文化旅游区", "三亚湾椰梦长廊"},
                "第一市场", "三亚国际免税城", "大东海海滩", "解放路步行街",
                "建议租车自驾游玩，机场有三亚凤凰国际机场。各景点间有旅游专线。",
                "亚龙湾/海棠湾/大东海区域",
                "海鲜大餐、文昌鸡、椰子鸡、清补凉、抱罗粉、陵水酸粉",
                "第一市场海鲜加工、春园海鲜广场、嗲嗲的椰子鸡"
            );
        }
        // 默认目的地信息
        return new DestInfo(
            new String[]{"当地著名景区A", "历史文化遗址B", "自然风光区C", "特色博物馆D", "民俗文化村E"},
            "当地商业街", "标志性建筑", "文艺街区", "市中心商圈",
            "建议选择飞机或高铁前往，当地可选择地铁、公交或打车出行。",
            "市中心或景区附近商圈",
            "当地特色美食、地道小吃、传统名菜、风味烧烤",
            "当地知名餐厅、美食街、老字号店铺"
        );
    }

    /**
     * 目的地信息内部类
     */
    private record DestInfo(
        String[] attractions,
        String shoppingArea,
        String landmark,
        String district,
        String transportTip,
        String accommodationArea,
        String foodieAdvice,
        String dinnerFood,
        String... snacks
    ) {}
}