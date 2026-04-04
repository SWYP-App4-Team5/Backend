package com.jjanpot.server.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "알림 설정 수정 요청")
public record NotificationSettingUpdateRequest(

	@Schema(description = "1일 1회 미인증 알림 활성화 여부", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
	boolean dailyEnabled,

	@Schema(description = "주간 미인증 알림 활성화 여부", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
	boolean weeklyEnabled,

	@Schema(description = "마케팅 수신 동의 여부", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
	boolean marketingConsent
) {
}
