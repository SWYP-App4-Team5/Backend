package com.jjanpot.server.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record DevLoginRequest(
	@Schema(
		description = "소셜 로그인 플랫폼 (KAKAO | GOOGLE | APPLE)",
		example = "KAKAO",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	@NotBlank(message = "provider는 필수입니다.")
	String provider,

	@Schema(
		description = "소셜 플랫폼의 사용자 고유 ID",
		example = "dev_user_001",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	@NotBlank(message = "providerId는 필수입니다.")
	String providerId,

	@Schema(
		description = "닉네임",
		example = "테스트유저",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	@NotBlank(message = "nickname은 필수입니다.")
	String nickname,

	@Schema(
		description = "이메일",
		example = "test@jjanpot.com",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED,
		nullable = true
	)
	String email,

	@Schema(
		description = "프로필 이미지 URL",
		example = "https://example.com/profile.jpg",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED,
		nullable = true
	)
	String profileImageUrl,

	@Schema(
		description = "기기 고유 식별자 (테스트용 아무 값 입력 가능)",
		example = "test-device-uuid",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED,
		nullable = true
	)
	String deviceUuid,

	@Schema(
		description = "FCM 푸시 토큰 (테스트용 아무 값 입력 가능)",
		example = "test-fcm-token",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED,
		nullable = true
	)
	String fcmToken
) {
}
