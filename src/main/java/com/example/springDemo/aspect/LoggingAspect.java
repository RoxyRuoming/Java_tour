package com.example.springDemo.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component // Spring 注解，让Spring容器管理这个类/ 让Spring自动发现并注册这个类为Bean
// 如果没有@Component注解，Spring不会自动识别并注册这个类为Bean，这会导致AOP功能无法生效
public class LoggingAspect {

  // 创建一个与LoggingAspect类关联的日志记录器
  private final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

  @AfterThrowing( // this is an advice: 在异常发生后执行日志记录
      pointcut = "execution(* com.example.springDemo.controller.*.*(..))", // targets all methods in all controller classes
      throwing = "exception" // 抛出的异常绑定到名为"exception"的参数
  )
  public void logAfterThrowingException(JoinPoint joinPoint, Exception exception) {
    logger.error("Exception in {}.{}() with cause = '{}'",
        joinPoint.getSignature().getDeclaringTypeName(), // Provides information about the method where the exception occurred
        joinPoint.getSignature().getName(),
        exception.getMessage() != null ? exception.getMessage() : "NULL");

    // If you want to log the stack trace as well
    logger.error("Exception stack trace: ", exception);
  }
}