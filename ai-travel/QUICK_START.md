# 🚀 快速启动指南

## ⚡ 5分钟快速启动

### 1. 安装 Maven（如果未安装）

**检查是否已安装：**
```bash
mvn -v
```

**安装（如未安装）：**
- Mac: `brew install maven`
- Ubuntu: `sudo apt-get install maven`
- Windows: 下载 https://maven.apache.org/download.cgi

### 2. 配置环境变量

**Linux/Mac:**
```bash
cd ai-travel
export DB_PASSWORD=123456
export JWT_SECRET=ai-travel-jwt-secret-key-2024!@#$
```

**Windows PowerShell:**
```powershell
cd ai-travel
$env:DB_PASSWORD = "123456"
$env:JWT_SECRET = "ai-travel-jwt-secret-key-2024!@#$"
```

> 💡 开发环境已配置默认值，可以跳过此步骤直接运行

### 3. 启动 MySQL

确保MySQL服务正在运行：
```bash
# Mac
brew services start mysql

# Linux
sudo systemctl start mysql

# Windows
# 在服务管理器中启动MySQL服务
```

### 4. 初始化数据库

```bash
mysql -u root -p < database/init.sql
```

### 5. 启动 Nacos

```bash
# 下载 Nacos (如果还没下载)
# https://github.com/alibaba/nacos/releases

cd nacos/bin
./startup.sh -m standalone
```

访问: http://localhost:8848/nacos

### 6. 编译项目

```bash
cd ai-travel
mvn clean compile -DskipTests
```

### 7. 启动服务

**在 IntelliJ IDEA / Eclipse 中：**
1. 打开项目（选择 `ai-travel/pom.xml`）
2. 找到 `GatewayApplication.java`
3. 右键 → Run

**或使用命令行：**

打开6个终端，分别运行：

```bash
# 终端1 - 用户服务
cd ai-travel
mvn spring-boot:run -pl ai-travel-user-service -Dspring-boot.run.profiles=dev

# 终端2 - 产品服务
mvn spring-boot:run -pl ai-travel-product-service -Dspring-boot.run.profiles=dev

# 终端3 - 行程服务
mvn spring-boot:run -pl ai-travel-plan-service -Dspring-boot.run.profiles=dev

# 终端4 - 订单服务
mvn spring-boot:run -pl ai-travel-order-service -Dspring-boot.run.profiles=dev

# 终端5 - AI服务
mvn spring-boot:run -pl ai-travel-ai-service -Dspring-boot.run.profiles=dev

# 终端6 - 网关（最后启动）
mvn spring-boot:run -pl ai-travel-gateway -Dspring-boot.run.profiles=dev
```

### 8. 启动前端

```bash
cd ai-travel/ai-travel-ui
npm install
npm run dev
```

访问: http://localhost:5173

## ✅ 验证服务

```bash
# 检查网关健康状态
curl http://localhost:8090/actuator/health

# 检查用户服务
curl http://localhost:8081/actuator/health

# 登录测试
curl -X POST http://localhost:8090/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Test123!"}'
```

## 📚 API文档

启动后访问：

- http://localhost:8090/swagger-ui.html （网关）
- http://localhost:8081/swagger-ui.html （用户服务）
- http://localhost:8082/swagger-ui.html （产品服务）
- http://localhost:8083/swagger-ui.html （行程服务）
- http://localhost:8084/swagger-ui.html （订单服务）
- http://localhost:8085/swagger-ui.html （AI服务）

## 🔑 测试账号

数据库初始化后可以使用以下测试账号（需要在数据库中手动创建或通过注册接口）：

```
用户名: admin
密码: Admin123! (需要满足密码强度要求)
```

## ❓ 遇到问题？

### Maven 编译慢
添加国内镜像到 `~/.m2/settings.xml`

### MySQL 连接失败
检查MySQL服务是否启动：`sudo systemctl status mysql`

### Nacos 连接失败
检查Nacos是否启动：http://localhost:8848/nacos

### 端口被占用
修改对应服务的 `application-dev.yml` 中的端口配置

## 📖 更多信息

详细文档请查看：
- [README.md](README.md) - 完整项目文档
- [CHANGELOG.md](CHANGELOG.md) - 更新日志
- [PROJECT_STATUS.md](PROJECT_STATUS.md) - 项目状态

---

**提示**: 开发环境已配置默认值，可以直接运行无需额外配置！
