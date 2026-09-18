# Maven Wrapper 配置指南

## 问题说明
IntelliJ IDEA 提示 `D:\JavaTools\apache-maven-3.9.16 is not correct maven home`，这是因为该路径不存在。

## 解决方案

### 方案1：使用 IntelliJ 内置 Maven（推荐，最简单）

内置 Maven 完全正常工作，只是配置路径不匹配：

1. **忽略警告**，直接使用内置 Maven 构建项目
2. 打开 IntelliJ 右侧的 **Maven** 面板
3. 展开 `ai-travel` → `Lifecycle`
4. 依次双击 `clean` → `install` 进行构建

### 方案2：配置正确的 Maven 路径

如果需要使用自己的 Maven 安装：

**步骤 A - 安装 Maven：**

使用 Chocolatey 安装（推荐）：
```powershell
choco install maven
```

或手动下载安装：
1. 访问 https://maven.apache.org/download.cgi
2. 下载 `apache-maven-3.9.6-bin.zip`
3. 解压到 `C:\Program Files\apache-maven-3.9.6`

**步骤 B - 在 IntelliJ 中配置：**

1. 打开 `File` → `Settings` → `Build, Execution, Deployment` → `Build Tools` → `Maven`
2. 设置 **Maven home directory**：
   - 如果你用 Chocolatey 安装：`C:\ProgramData\chocolatey\lib\maven\apache-maven-3.9.6`
   - 如果手动安装：`C:\Program Files\apache-maven-3.9.6`
3. 点击 **Apply** → **OK**

### 方案3：使用 Maven Wrapper（团队协作推荐）

项目已配置 Maven Wrapper，团队成员无需安装 Maven：

```bash
cd "C:\Users\15119\Desktop\作业\认识实习\ai-travel\ai-travel\ai-travel"

# 使用 wrapper 构建（首次会自动下载 Maven）
./mvnw clean install

# Windows 使用
mvnw.cmd clean install
```

首次运行会下载 Maven 3.9.6 到 `.mvn/wrapper` 目录。

## 验证构建

无论使用哪种方案，都可以通过以下命令验证：

```bash
mvn clean compile
```

或使用 IntelliJ 的 Maven 面板进行构建。

## 项目结构

```
ai-travel/
├── ai-travel-common/          # 通用模块
├── ai-travel-gateway/         # 网关模块
├── ai-travel-user-service/    # 用户服务
├── ai-travel-product-service/ # 产品服务
├── ai-travel-plan-service/    # 规划服务
├── ai-travel-order-service/   # 订单服务
└── ai-travel-ai-service/      # AI服务
```
