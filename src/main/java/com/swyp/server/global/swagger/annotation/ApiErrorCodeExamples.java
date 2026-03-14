package com.swyp.server.global.swagger.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.swyp.server.global.exception.ErrorCode;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiErrorCodeExamples {

	Class<? extends ErrorCode>[] value();

	String[] include() default {};

	// 포함된 모든 에러 예시에 FieldError(errors)를 보여줄지 여부
	boolean isValidationError() default false;
}
