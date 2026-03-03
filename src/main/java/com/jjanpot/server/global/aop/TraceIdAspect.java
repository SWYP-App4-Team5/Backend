package com.jjanpot.server.global.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@Order(1)
public class TraceIdAspect {
	@Around("execution(* com.jjanpot.server..controller..*(..)) || execution(* com.jjanpot.server..service..*(..))")
	public Object setTraceId(ProceedingJoinPoint joinPoint) throws Throwable {
		boolean isNewTrace = MdcTraceId.putIfAbsent();
		try {
			return joinPoint.proceed();
		} finally {
			if (isNewTrace) {
				MdcTraceId.remove();
			}
		}
	}
}
