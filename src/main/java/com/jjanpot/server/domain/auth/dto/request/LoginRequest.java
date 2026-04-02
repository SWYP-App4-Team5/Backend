package com.jjanpot.server.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
	@NotBlank
	String accessToken,
	@NotBlank
	String deviceUuid,
	@NotBlank
	String fcmToken
) {
}
