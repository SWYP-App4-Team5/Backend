package com.jjanpot.server.global.swagger.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jjanpot.server.global.exception.ErrorCode;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiErrorCodeExample {

	Class<? extends ErrorCode> value();

	String[] include() default {};

	// 필드 에러(errors) 예시를 포함할지 여부
	boolean isValidationError() default false;
}
