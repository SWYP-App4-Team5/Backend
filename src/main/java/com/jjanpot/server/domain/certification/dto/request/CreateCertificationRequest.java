package com.jjanpot.server.domain.certification.dto.request;

import java.time.LocalDateTime;

import com.jjanpot.server.domain.certification.entity.SpendType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCertificationRequest(

	@Schema(description = "챌린지 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "챌린지 ID는 필수입니다.")
	Long challengeId,

	@Schema(
		description = "지출 유형 (SPEND: 지출 | NO_SPEND: 무지출)",
		example = "SPEND",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	@NotNull(message = "지출 유형은 필수입니다.")
	SpendType spendType,

	@Schema(
		description = "카테고리 ID (챌린지에 설정된 카테고리 중 선택)",
		example = "1",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	@NotNull(message = "카테고리 ID는 필수입니다.")
	Long categoryId,

	@Schema(
		description = "오늘 사용한 금액 (SPEND일 때 필수, NO_SPEND면 null 또는 0)",
		example = "3500",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED,
		nullable = true
	)
	@Min(value = 0, message = "소비 금액은 0원 이상이어야 합니다.")
	Integer spentAmount,

	@Schema(
		description = "메모 (최대 30자, 선택 입력)",
		example = "텀블러에 담아서 먹었는데 그럭저럭",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED,
		nullable = true
	)
	@Size(max = 30, message = "메모는 30자 이하여야 합니다.")
	String memo,

	@Schema(
		description = "지출 일시 (사용자가 직접 선택 가능)",
		example = "2026-03-29T10:30:00",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	@NotNull(message = "지출 일시는 필수입니다.")
	LocalDateTime spentAt
) {
}
