package com.ai.travel.plan.exception;

import com.ai.travel.common.exception.BaseGlobalExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * 行程服务全局异常处理器
 */
@ControllerAdvice
public class GlobalExceptionHandler extends BaseGlobalExceptionHandler {

    // 继承基类的统一异常处理逻辑
}
