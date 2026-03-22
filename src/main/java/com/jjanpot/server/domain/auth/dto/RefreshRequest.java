package com.jjanpot.server.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
	@NotBlank(message = "리프레시 토큰은 필수 값입니다")
	String refreshToken
) {
}
