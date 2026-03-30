package com.jjanpot.server.domain.certification.dto.response;

import com.jjanpot.server.domain.certification.entity.Certification;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인증 생성 응답")
public record CreateCertificationResponse(

	@Schema(description = "생성된 인증 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	Long certificationId,

	@Schema(description = "절약 금액 (기준 금액 - 소비 금액, 음수 가능)", example = "11500", requiredMode = Schema.RequiredMode.REQUIRED)
	int savedAmount
) {

	public static CreateCertificationResponse from(Certification certification) {
		return new CreateCertificationResponse(
			certification.getCertificationId(),
			certification.getSavedAmount()
		);
	}
}
