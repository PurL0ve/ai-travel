@echo off
REM ================================
REM AI Travel Platform 代码质量验证脚本 (Windows)
REM ================================

echo ==========================================
echo   AI Travel Platform - 代码质量检查
echo ==========================================
echo.

set PASS_COUNT=0
set FAIL_COUNT=0
set WARN_COUNT=0

REM 检查函数
:check_pass
echo [PASS] %1
set /a PASS_COUNT+=1
goto :eof

:check_fail
echo [FAIL] %1
set /a FAIL_COUNT+=1
goto :eof

:check_warn
echo [WARN] %1
set /a WARN_COUNT+=1
goto :eof

echo 1. 检查敏感信息硬编码...
echo ------------------------------------------

REM 检查是否有明文密码（简化版）
findstr /s /m "password: 123456" *.yml *.yaml *.properties 2>nul | findstr /v "example" >nul
if %errorlevel% equ 0 (
    call :check_fail "发现明文数据库密码"
) else (
    call :check_pass "未发现明文数据库密码"
)

echo.
echo 2. 检查环境配置文件...
echo ------------------------------------------

if exist "ai-travel-user-service\src\main\resources\application.yml" (
    call :check_pass "ai-travel-user-service 有基础配置"
) else (
    call :check_fail "ai-travel-user-service 缺少 application.yml"
)

if exist "ai-travel-user-service\src\main\resources\application-dev.yml" (
    call :check_pass "ai-travel-user-service 有开发环境配置"
) else (
    call :check_warn "ai-travel-user-service 缺少 application-dev.yml"
)

if exist "ai-travel-user-service\src\main\resources\application-prod.yml" (
    call :check_pass "ai-travel-user-service 有生产环境配置"
) else (
    call :check_warn "ai-travel-user-service 缺少 application-prod.yml"
)

echo.
echo 3. 检查文档...
echo ------------------------------------------

if exist "README.md" (
    call :check_pass "README.md 存在"
) else (
    call :check_fail "README.md 不存在"
)

if exist ".env.example" (
    call :check_pass ".env.example 环境变量示例存在"
) else (
    call :check_warn ".env.example 不存在"
)

if exist "CHANGELOG.md" (
    call :check_pass "CHANGELOG.md 存在"
) else (
    call :check_warn "CHANGELOG.md 不存在"
)

if exist "database\init.sql" (
    call :check_pass "数据库初始化脚本存在"
) else (
    call :check_warn "数据库初始化脚本不存在"
)

echo.
echo ==========================================
echo   检查完成
echo ==========================================
echo 通过: %PASS_COUNT%
echo 失败: %FAIL_COUNT%
echo 警告: %WARN_COUNT%
echo.

if %FAIL_COUNT% equ 0 (
    echo [OK] 所有关键检查通过！
    exit /b 0
) else (
    echo [ERROR] 发现 %FAIL_COUNT% 个问题需要修复
    exit /b 1
)
