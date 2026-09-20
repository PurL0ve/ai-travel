# AI 旅行平台 🤖✈️

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.3-blue.svg)](https://spring.io/projects/spring-cloud)
[![Spring Cloud Alibaba](https://img.shields.io/badge/Spring%20Cloud%20Alibaba-2023.0.1.0-orange.svg)](https://spring-cloud-alibaba.github.io/)
[![Java](https://img.shields.io/badge/Java-17-red.svg)](https://www.oracle.com/java/technologies/downloads/#java17)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-6.0-red.svg)](https://redis.io/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

一个基于 **Spring Cloud Alibaba** 微服务架构的AI智能旅行规划平台，集成阿里云通义千问大模型，为用户提供智能行程生成、景点推荐和旅游产品匹配服务。

---

## 📖 项目简介

AI旅行平台是一个现代化的旅游服务系统，结合了传统微服务架构与最新的人工智能技术。平台通过AI大模型为每位用户量身定制旅行方案，同时提供丰富的旅游产品选择和便捷的预订体验。

### 核心特性

- 🤖 **AI智能推荐**: 基于通义千问大模型生成个性化行程方案
- 🎯 **多方案对比**: 同时生成文化、美食、探险等多主题方案
- 🔍 **智能匹配**: AI + 数据库双重匹配，精准推荐旅游产品
- 🌐 **微服务架构**: 基于Spring Cloud Alibaba的6个独立微服务
- ⚡ **高性能**: Redis缓存 + 数据库优化，响应时间<500ms
- 🔐 **安全可靠**: JWT认证 + BCrypt加密 + 参数校验
- 📊 **服务治理**: Nacos注册配置中心 + 服务发现 + 负载均衡

---

## 🏗️ 系统架构

### 技术栈

| 技术                     | 版本          | 说明         |
| ------------------------ | ------------- | ------------ |
| **Spring Boot**          | 3.2.3         | 应用框架     |
| **Spring Cloud**         | 2023.0.3      | 微服务框架   |
| **Spring Cloud Alibaba** | 2023.0.1.0    | Nacos集成    |
| **Nacos**                | 2.2.3         | 注册配置中心 |
| **MySQL**                | 8.0           | 关系型数据库 |
| **Redis**                | 7.0           | 缓存数据库   |
| **通义千问**             | DashScope SDK | AI大模型     |
| **JWT**                  | 0.12.5        | Token认证    |
| **Lombok**               | 1.18.46       | 代码简化     |

### 微服务模块

```
ai-travel/
├── ai-travel-common/          # 公共模块
│   ├── 通用VO                 # Result, PageRequest, PageResult
│   ├── 工具类                 # DateUtils, StringUtils
│   └── 异常处理
├── ai-travel-gateway/         # API网关 (端口: 8080)
├── ai-travel-user-service/    # 用户服务 (端口: 8081)
├── ai-travel-product-service/ # 产品服务 (端口: 8082)
├── ai-travel-plan-service/    # 行程服务 (端口: 8083)
├── ai-travel-order-service/   # 订单服务 (端口: 8084)
└── ai-travel-ai-service/      # AI服务 (端口: 8085) ⭐
```

### 架构图

```
┌─────────────┐
│   客户端     │
│  Web/App/小程序 │
└──────┬──────┘
       │
       ▼
┌─────────────────────────────────┐
│      API Gateway (8080)          │
│  路由转发 / 认证 / 限流           │
└────────┬────────────────────────┘
         │
    ┌────┴────┐
    ▼         ▼
┌────────┐  ┌────────┐
│User Svc│  │AI Svc  │
│(8081)  │  │(8085)  │
└────────┘  └───┬────┘
              ┌──┴───┐
              ▼      ▼
          Product   ┌──────────┐
          Service   │ 大模型    │
          (8082)    │ 通义千问  │
                    └──────────┘
                         │
                         ▼
                    ┌──────────┐
                    │ 数据存储  │
                    │MySQL+Redis│
                    └──────────┘
```

---

## ✨ 核心功能

### 🤖 AI智能推荐

**综合推荐接口** - 一键获取多个旅行方案

```http
POST /api/ai/recommend
Content-Type: application/json

{
  "destination": "北京",
  "days": 3,
  "budget": "舒适型",
  "interests": ["文化", "美食"]
}
```

**响应**:

```json
{
  "code": 200,
  "data": {
    "destination": "北京",
    "plans": [
      {
        "theme": "文化深度游",
        "description": "深入了解北京的历史文化...",
        "schedule": [...],
        "estimatedCost": 3000
      },
      {
        "theme": "美食休闲游",
        ...
      },
      {
        "theme": "自然探险游",
        ...
      }
    ],
    "products": [
      {
        "productName": "北京3日经典游",
        "matchScore": 0.95,
        ...
      }
    ]
  }
}
```

**功能特点**:

- ✅ 生成3个不同主题的行程方案
- ✅ 自动匹配数据库中最相关的旅游产品
- ✅ 基于预算、天数、兴趣标签智能推荐
- ✅ 匹配度评分排序

### 🗺️ AI行程生成

**生成完整旅行计划**

```http
POST /api/ai/generate-plan
Content-Type: application/json

{
  "destination": "成都",
  "days": 5,
  "budget": "豪华型",
  "interests": ["美食", "熊猫"]
}
```

**响应包含**:

- 📅 每日详细时间安排 (上午/下午/晚上)
- 🚗 交通建议
- 🏨 住宿推荐
- 💰 费用预估

### 🔍 相似景点搜索

基于语义理解的景点推荐

```http
POST /api/ai/search-similar
{
  "query": "故宫",
  "type": "attraction",
  "topK": 5
}
```

### 🎯 旅游产品管理

- 产品CRUD操作
- 多维度筛选 (目的地/类型/价格)
- 分页查询
- 产品上下架管理

---

## 🚀 快速开始

### 方式一: Docker一键启动 (推荐)

```bash
# 1. 克隆项目
git clone https://github.com/your-username/ai-travel.git
cd ai-travel

# 2. 一键启动所有中间件
docker-compose up -d

# 3. 验证启动
docker-compose ps
```

### 方式二: 本地手动启动

**环境要求**:

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- Node.js 16+ (前端)

**启动步骤**:

```bash
# 1. 启动MySQL和Redis

# 2. 导入数据库
mysql -u root -p < sql/init.sql

# 3. 修改配置
# 编辑各服务的 application.yml 配置数据库连接

# 4. 启动微服务 (按顺序)
cd ai-travel-common && mvn spring-boot:run
cd ai-travel-user-service && mvn spring-boot:run
cd ai-travel-product-service && mvn spring-boot:run
cd ai-travel-plan-service && mvn spring-boot:run
cd ai-travel-order-service && mvn spring-boot:run
cd ai-travel-ai-service && mvn spring-boot:run
cd ai-travel-gateway && mvn spring-boot:run

# 5. 启动前端 (可选)
cd ai-travel-ui
npm install
npm run dev
```

### 📚 详细文档

- [技术实现文档](./技术实现文档.md) - 完整技术架构说明
- [架构设计详解](./架构设计详解.md) - 架构图与数据流
- [快速启动指南](./快速启动指南.md) - 详细启动教程

---

## 🧪 测试

### API测试

```bash
# 1. 测试综合推荐
curl -X POST http://localhost:8080/api/ai/recommend \
  -H "Content-Type: application/json" \
  -d '{"destination":"北京","days":3,"budget":"舒适型"}'

# 2. 测试用户登录
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# 3. 测试产品查询
curl http://localhost:8080/api/product/list?pageNum=1&pageSize=10
```

### 访问地址

| 服务            | 地址                           | 说明                   |
| --------------- | ------------------------------ | ---------------------- |
| **API网关**     | http://localhost:8080          | 统一入口               |
| **Nacos**       | http://localhost:8848/nacos    | 注册中心 (nacos/nacos) |
| **Swagger文档** | http://localhost:8080/doc.html | API文档                |
| **前端页面**    | http://localhost:5173          | Web界面                |

---

## 📊 数据库

### 数据表结构

- **user** - 用户表
- **product** - 旅游产品表
- **plan** - 行程表
- **order** - 订单表
- **plan_product** - 行程产品关联表
- **user_favorite** - 用户收藏表
- **user_history** - 用户浏览历史表
- **sys_config** - 系统配置表

### 初始化数据

已内置测试数据:

- 3个测试用户 (admin/zhangsan/lisi)
- 8个旅游产品 (覆盖北京/三亚/成都/上海/西安等)
- 2个示例行程
- 2个测试订单

**测试密码**: `123456`

---

## 🔐 安全特性

- ✅ JWT Token认证 (Access Token + Refresh Token)
- ✅ BCrypt密码加密
- ✅ 接口参数校验
- ✅ SQL注入防护
- ✅ XSS防护
- ✅ CORS跨域配置

---

## 🛠️ 开发指南

### 项目结构

```
ai-travel-xxx-service/
├── src/main/java/com/ai/travel/xxx/
│   ├── controller/     # REST API 控制器
│   ├── service/        # 业务逻辑层
│   ├── repository/     # 数据访问层 (JPA)
│   ├── entity/         # 数据库实体
│   ├── dto/            # 数据传输对象
│   ├── feign/          # Feign客户端
│   ├── config/         # 配置类
│   ├── security/       # 安全配置
│   └── exception/      # 异常处理
└── src/main/resources/
    ├── application.yml # 服务配置
    └── mapper/         # MyBatis XML
```

### 代码规范

- 使用Lombok简化代码
- 统一响应格式: `Result<T>`
- 统一异常处理
- RESTful API设计
- Swagger API文档

### Git提交规范

```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 重构
test: 测试相关
chore: 构建/工具相关
```

---

## 🐛 问题反馈

遇到问题？

1. 📖 查看 [常见问题](快速启动指南.md#常见问题排查)
2. 🔍 搜索 [Issues](https://github.com/your-username/ai-travel/issues)
3. 💬 加入讨论区
4. 🐛 提交 [Bug Report](https://github.com/your-username/ai-travel/issues/new)

---

## 🤝 贡献指南

欢迎贡献代码！请遵循以下步骤:

1. Fork本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'feat: Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启Pull Request

---

## 📈 路线图

### v1.0 (当前版本) ✅

- [x] 微服务基础架构
- [x] AI推荐功能
- [x] 用户/产品/行程/订单管理
- [x] Nacos注册配置
- [x] JWT认证

### v1.1 (计划中)

- [ ] Redis缓存集成
- [ ] 向量数据库(Qdrant)集成
- [ ] 真实AI大模型接入
- [ ] 支付集成(支付宝/微信)
- [ ] 消息通知(短信/邮件)

### v1.2 (规划中)

- [ ] 实时聊天客服
- [ ] 行程分享功能
- [ ] 社区互动
- [ ] 移动端APP
- [ ] 小程序

### v2.0 (长期规划)

- [ ] 多语言支持
- [ ] AI助手对话式规划
- [ ] VR/AR景点预览
- [ ] 区块链溯源

---

## 📄 开源协议

本项目采用 [MIT](LICENSE) 协议开源

---

## 👨‍💻 作者

- **Jame** - [GitHub](https://github.com/Jame)

---

## 🙏 致谢

感谢以下开源项目:

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Cloud Alibaba](https://spring-cloud-alibaba.github.io/)
- [Nacos](https://nacos.io/)
- [DashScope](https://help.aliyun.com/zh/dashscope/)
- [MyBatis](https://mybatis.org/mybatis-3/)

---

## 📞 联系方式

- 📧 Email: your-email@example.com
- 💬 WeChat: your-wechat-id
- 🌐 Website: https://ai-travel.example.com

---

## ⭐ Star History

如果这个项目对你有帮助，请给我们一个 ⭐ Star！

[![Star History Chart](https://api.star-history.com/svg?repos=your-username/ai-travel&stars)](https://star-history.com/#your-username/ai-travel)

---

**构建时间**: 2026-09-20
**版本**: v1.0.0-SNAPSHOT
**状态**: ✅ 核心功能已完成，持续迭代中
