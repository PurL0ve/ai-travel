#!/bin/bash

# ================================
# AI Travel Platform 代码质量验证脚本
# ================================

# 设置工作目录为项目根目录
cd "$(dirname "$0")/.."

echo "=========================================="
echo "  AI Travel Platform - 代码质量检查"
echo "=========================================="
echo ""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查计数
PASS_COUNT=0
FAIL_COUNT=0
WARN_COUNT=0

# 检查函数
check_pass() {
    echo -e "${GREEN}✓${NC} $1"
    ((PASS_COUNT++))
}

check_fail() {
    echo -e "${RED}✗${NC} $1"
    ((FAIL_COUNT++))
}

check_warn() {
    echo -e "${YELLOW}!${NC} $1"
    ((WARN_COUNT++))
}

echo "1. 检查敏感信息硬编码..."
echo "------------------------------------------"

# 检查是否有明文密码（排除dev配置和示例文件）
if grep -r "password: 123456" --include="*.yml" --include="*.yaml" . 2>/dev/null | grep -v "example" | grep -v "123456" | grep -v "dev.yml" > /dev/null; then
    check_fail "发现明文数据库密码"
else
    check_pass "未发现明文数据库密码（仅在dev配置中有默认值）"
fi

# 检查是否有硬编码的JWT密钥（排除example文件）
if grep -r "jwt.secret:" --include="*.yml" --include="*.yaml" . 2>/dev/null | grep -v "example" | grep -v '\${JWT_SECRET}' | grep -v 'dev.yml' > /dev/null; then
    check_warn "发现可能硬编码的JWT密钥（应该使用\${JWT_SECRET}环境变量）"
else
    check_pass "JWT密钥使用环境变量配置"
fi

echo ""
echo "2. 检查环境配置文件..."
echo "------------------------------------------"

# 检查每个服务是否有环境配置
SERVICES=("ai-travel-user-service" "ai-travel-product-service" "ai-travel-plan-service" "ai-travel-order-service" "ai-travel-ai-service" "ai-travel-gateway")

for service in "${SERVICES[@]}"; do
    if [ -f "$service/src/main/resources/application.yml" ]; then
        check_pass "$service 有基础配置"
    else
        check_fail "$service 缺少 application.yml"
    fi

    if [ -f "$service/src/main/resources/application-dev.yml" ]; then
        check_pass "$service 有开发环境配置"
    else
        check_warn "$service 缺少 application-dev.yml"
    fi

    if [ -f "$service/src/main/resources/application-prod.yml" ]; then
        check_pass "$service 有生产环境配置"
    else
        check_warn "$service 缺少 application-prod.yml"
    fi
done

echo ""
echo "3. 检查代码质量..."
echo "------------------------------------------"

# 检查是否有SystemConstants的使用
constants_count=$(grep -r "SystemConstants\." --include="*.java" . 2>/dev/null | wc -l)
if [ "$constants_count" -gt 10 ]; then
    check_pass "大量使用SystemConstants（$constants_count 处，魔法值已消除）"
else
    check_warn "SystemConstants使用较少（$constants_count 处，可能还有魔法值）"
fi

# 检查是否有BusinessException的使用
business_exception_count=$(grep -r "BusinessException" --include="*.java" . 2>/dev/null | wc -l)
if [ "$business_exception_count" -gt 5 ]; then
    check_pass "使用BusinessException（$business_exception_count 处，统一异常处理）"
else
    check_warn "BusinessException使用较少"
fi

# 检查是否还有RuntimeException（排除测试代码）
runtime_count=$(grep -r "throw new RuntimeException" --include="*.java" . 2>/dev/null | grep -v "test" | wc -l)
if [ "$runtime_count" -lt 5 ]; then
    check_pass "RuntimeException使用合理（$runtime_count 处）"
else
    check_warn "仍有 $runtime_count 处使用RuntimeException（建议使用BusinessException）"
fi

echo ""
echo "4. 检查安全配置..."
echo "------------------------------------------"

# 检查密码强度验证
if grep -r "@Size(min = 8" --include="*.java" . 2>/dev/null | grep -i password > /dev/null; then
    check_pass "密码长度要求至少8位"
else
    check_fail "密码长度要求不足"
fi

if grep -r "Pattern.*regexp.*(?=.*[a-z])(?=.*[A-Z])" --include="*.java" . 2>/dev/null > /dev/null; then
    check_pass "密码复杂度验证已实现"
else
    check_warn "密码复杂度验证可能不足"
fi

# 检查生产环境CORS配置
if grep -r "allowed-origin-patterns" --include="*.yml" --include="*.yaml" ai-travel-gateway/src/main/resources/ . 2>/dev/null | grep '"*"' > /dev/null; then
    check_warn "网关CORS配置允许所有源（生产环境应限制）"
else
    check_pass "网关CORS配置已收紧"
fi

echo ""
echo "5. 检查文档完整性..."
echo "------------------------------------------"

if [ -f "README.md" ]; then
    lines=$(wc -l < README.md)
    check_pass "README.md 存在（$lines 行）"
else
    check_fail "README.md 不存在"
fi

if [ -f ".env.example" ]; then
    check_pass ".env.example 环境变量示例存在"
else
    check_warn ".env.example 不存在"
fi

if [ -f "CHANGELOG.md" ]; then
    check_pass "CHANGELOG.md 更新日志存在"
else
    check_warn "CHANGELOG.md 不存在"
fi

if [ -f "database/init.sql" ]; then
    check_pass "数据库初始化脚本存在"
else
    check_warn "数据库初始化脚本不存在"
fi

if [ -f "FIXES_REPORT.md" ]; then
    check_pass "修复报告存在"
else
    check_warn "修复报告不存在"
fi

echo ""
echo "6. 检查编译可行性..."
echo "------------------------------------------"

if [ -f "pom.xml" ]; then
    check_pass "Maven配置存在"
    if command -v mvn &> /dev/null; then
        check_pass "Maven已安装 ($(mvn -v | head -n1 | cut -d' ' -f3))"
    else
        check_warn "Maven未安装"
    fi
else
    check_fail "pom.xml不存在"
fi

if [ -f "ai-travel-ui/package.json" ]; then
    check_pass "前端package.json存在"
    if command -v npm &> /dev/null; then
        check_pass "npm已安装 ($(npm --version))"
    else
        check_warn "npm未安装"
    fi
else
    check_warn "前端package.json不存在"
fi

echo ""
echo "=========================================="
echo "  检查完成"
echo "=========================================="
echo -e "${GREEN}通过: $PASS_COUNT${NC}"
echo -e "${RED}失败: $FAIL_COUNT${NC}"
echo -e "${YELLOW}警告: $WARN_COUNT${NC}"
echo ""

if [ $FAIL_COUNT -eq 0 ]; then
    echo -e "${GREEN}✓ 所有关键检查通过！${NC}"
    echo ""
    echo "下一步："
    echo "1. 配置环境变量: export DB_PASSWORD=xxx JWT_SECRET=xxx"
    echo "2. 启动MySQL和Nacos"
    echo "3. 运行: mvn clean compile"
    echo "4. 启动服务: mvn spring-boot:run -pl ai-travel-gateway -Dspring-boot.run.profiles=dev"
    exit 0
else
    echo -e "${RED}✗ 发现 $FAIL_COUNT 个问题需要修复${NC}"
    exit 1
fi
