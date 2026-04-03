package com.jjanpot.server.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CodeLoginRequest(
	@NotBlank(message = "인가코드는 필수입니다.")
	String code,

	@NotBlank(message = "redirectUri는 필수입니다.")
	String redirectUri,

	@NotBlank(message = "deviceUuid는 필수입니다.")
	String deviceUuid,

	@NotBlank(message = "fcmToken은 필수입니다.")
	String fcmToken
) {
}
