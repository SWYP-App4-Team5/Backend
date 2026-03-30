package com.jjanpot.server.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "약관 동의 요청")
public record UserAgreementRequest(

	@Schema(description = "만 14세 이상 확인", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull
	Boolean ageVerified,

	@Schema(description = "이용약관 동의", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull
	Boolean termsOfServiceAgreed,

	@Schema(description = "개인정보처리방침 동의", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull
	Boolean privacyPolicyAgreed,

	@Schema(description = "마케팅 수신 동의 (선택)", example = "false", nullable = true)
	Boolean marketingConsent
) {
}
