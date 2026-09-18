# AI Travel Platform

AI旅行平台是一个基于微服务架构的智能旅行规划与预订系统。

## 🏗️ 项目架构

### 技术栈

- **后端框架**: Spring Boot 3.2.3 + Spring Cloud 2023.0.3
- **服务注册**: Alibaba Nacos
- **数据库**: MySQL 8.0
- **ORM框架**: Spring Data JPA (Hibernate)
- **安全认证**: Spring Security + JWT
- **API文档**: Swagger/OpenAPI 3
- **前端框架**: Vue 3 + Vite + Element Plus
- **构建工具**: Maven (后端) + npm (前端)

### 服务模块

| 服务名 | 端口 | 说明 |
|--------|------|------|
| ai-travel-gateway | 8090 | API网关 |
| ai-travel-user-service | 8081 | 用户服务 |
| ai-travel-product-service | 8082 | 产品服务 |
| ai-travel-plan-service | 8083 | 行程服务 |
| ai-travel-order-service | 8084 | 订单服务 |
| ai-travel-ai-service | 8085 | AI服务 |
| ai-travel-common | - | 公共模块 |

## 🚀 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Node.js 16+
- Nacos 2.2+ (用于服务发现和配置)

### 1. 数据库准备

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS ai_travel DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户（可选）
CREATE USER 'ai_travel'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON ai_travel.* TO 'ai_travel'@'localhost';
FLUSH PRIVILEGES;
```

### 2. 配置环境变量

#### 开发环境（推荐）

在`application-dev.yml`中配置（已预设默认值）：

```yaml
spring:
  datasource:
    username: root
    password: ${DB_PASSWORD:123456}
```

#### 生产环境

设置环境变量：

```bash
# Linux/Mac
export DB_PASSWORD=your_secure_password
export JWT_SECRET=your_jwt_secret_key_here
export NACOS_SERVER_ADDR=localhost:8848

# Windows (PowerShell)
$env:DB_PASSWORD = "your_secure_password"
$env:JWT_SECRET = "your_jwt_secret_key_here"
```

### 3. 启动Nacos

```bash
# 下载并启动Nacos
curl -O https://github.com/alibaba/nacos/releases/download/2.2.3/nacos-server-2.2.3.tar.gz
tar -zxvf nacos-server-2.2.3.tar.gz
cd nacos/bin
./startup.sh -m standalone
```

Nacos控制台: http://localhost:8848/nacos

### 4. 启动后端服务

```bash
# 进入项目根目录
cd ai-travel

# 编译项目
mvn clean compile

# 打包（可选）
mvn clean package -DskipTests

# 启动所有服务（开发模式，激活dev配置）
# 注意：每个服务需要单独启动
mvn spring-boot:run -pl ai-travel-user-service -Dspring-boot.run.profiles=dev
mvn spring-boot:run -pl ai-travel-product-service -Dspring-boot.run.profiles=dev
mvn spring-boot:run -pl ai-travel-plan-service -Dspring-boot.run.profiles=dev
mvn spring-boot:run -pl ai-travel-order-service -Dspring-boot.run.profiles=dev
mvn spring-boot:run -pl ai-travel-ai-service -Dspring-boot.run.profiles=dev
mvn spring-boot:run -pl ai-travel-gateway -Dspring-boot.run.profiles=dev
```

### 5. 启动前端

```bash
cd ai-travel/ai-travel-ui

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 或构建生产版本
npm run build
```

访问: http://localhost:5173

## 📚 API文档

服务启动后访问：

- **用户服务**: http://localhost:8081/swagger-ui.html
- **产品服务**: http://localhost:8082/swagger-ui.html
- **行程服务**: http://localhost:8083/swagger-ui.html
- **订单服务**: http://localhost:8084/swagger-ui.html
- **AI服务**: http://localhost:8085/swagger-ui.html

## 🔐 安全配置

### JWT配置

```yaml
jwt:
  secret: ${JWT_SECRET}  # 必须设置强密钥
  expire: 86400000       # Token过期时间（24小时）
  refresh-expire: 604800000  # Refresh Token过期时间（7天）
```

**生产环境必须：**
1. 使用强随机密钥（至少256位）
2. 启用HTTPS
3. 设置合理的Token过期时间
4. 实现Refresh Token机制

### 密码要求

注册和修改密码必须满足：
- 最少8位
- 包含大小写字母
- 包含数字
- 包含特殊字符 (@$!%*?&)

### 数据库安全

- 生产环境禁用`ddl-auto: update`
- 使用连接池（HikariCP已配置）
- 启用SSL连接
- 定期备份数据

## 🛠️ 开发指南

### 代码规范

1. **命名规范**
   - 类名：大驼峰（UserService）
   - 方法名：小驼峰（getUserInfo）
   - 常量：全大写下划线分隔（MAX_PAGE_SIZE）

2. **异常处理**
   - 使用`BusinessException`抛出业务异常
   - 统一由`BaseGlobalExceptionHandler`处理

3. **日志规范**
   - 使用Slf4j日志
   - 避免在日志中打印敏感信息
   - 适当使用日志级别（ERROR/WARN/INFO/DEBUG）

4. **注释规范**
   - 类和方法添加JavaDoc
   - 复杂业务逻辑添加行内注释
   - 使用中文注释

### 分页查询

使用`PageRequest`和`PageResult`：

```java
// 1. 创建分页请求
PageRequest pageRequest = new PageRequest();
pageRequest.setPageNum(1);
pageRequest.setPageSize(10);

// 2. 调用service
PageResult<Product> pageResult = productService.getProducts(pageRequest);

// 3. 返回给前端
return Result.success(pageResult);
```

### 服务间调用

使用Feign客户端（已配置）：

```java
// 定义Feign接口
@FeignClient(name = "product-service")
public interface ProductFeignClient {
    @GetMapping("/api/product/{id}")
    Result<ProductResponse> getProduct(@PathVariable("id") Long id);
}

// 注入使用
@Autowired
private ProductFeignClient productFeignClient;
```

## 🧪 测试

### 单元测试

```bash
# 运行所有测试
mvn test

# 运行指定模块测试
mvn test -pl ai-travel-user-service
```

### 接口测试

使用Postman或curl：

```bash
# 登录
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Password123!"}'

# 获取用户信息（需要Token）
curl -X GET http://localhost:8080/api/user/info \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 🚢 部署

### Docker部署（推荐）

```bash
# 构建镜像
mvn clean package -DskipTests -Pprod
docker build -t ai-travel .

# 使用Docker Compose启动
docker-compose up -d
```

### 手动部署

1. 打包所有服务：`mvn clean package -DskipTests -Pprod`
2. 上传jar包到服务器
3. 配置环境变量
4. 启动服务：`java -jar ai-travel-user-service.jar --spring.profiles.active=prod`

## 📊 监控与日志

### 日志配置

日志按天滚动，保留30天：

```yaml
logging:
  file:
    name: logs/ai-travel-user-service.log
    max-size: 10MB
    max-history: 30
```

### 健康检查

访问：http://localhost:8081/actuator/health

## 🤝 贡献

1. Fork本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启Pull Request

## 📄 许可证

本项目采用 MIT 许可证

## 📞 联系方式

- 项目主页: https://github.com/yourusername/ai-travel
- 问题反馈: https://github.com/yourusername/ai-travel/issues

---

**注意**: 本项目的所有API密钥、数据库密码等敏感信息必须通过环境变量配置，严禁硬编码在代码中。
