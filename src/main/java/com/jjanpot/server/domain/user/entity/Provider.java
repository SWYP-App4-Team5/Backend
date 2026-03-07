package com.jjanpot.server.domain.user.entity;

import java.util.Arrays;

import com.jjanpot.server.global.exception.BusinessException;
import com.jjanpot.server.global.exception.ErrorCode;

public enum Provider {
	KAKAO,
	GOOGLE,
	NAVER,
	APPLE;

	public static Provider from(String value) {

		if(value == null || value.trim().isEmpty()) {
			throw new BusinessException(
				ErrorCode.INVALID_INPUT, "소셜로그인 플랫폼 입력은 필수입니다. ");
		}

		return Arrays.stream(values())
			.filter(provider -> provider.name().equalsIgnoreCase(value.trim()))
			.findFirst()
			.orElseThrow(() -> new BusinessException(
				ErrorCode.INVALID_INPUT, "지원하지않는 로그인 형식 입니다"));
	}
}
