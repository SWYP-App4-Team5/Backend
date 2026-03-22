package com.jjanpot.server.domain.challenge.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.jjanpot.server.domain.team.entity.TeamType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateChallengeRequest(

	// @Schema(description = "팀 이름", example = "자취방 절약단")
	// @NotBlank(message = "팀 이름은 필수입니다.")
	// @Size(max = 100, message = "팀 이름은 100자 이하여야 합니다.")
	// String teamName,

	@Schema(
		description = "챌린지 제목",
		example = "이번 주 외식비 절약 챌린지",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	@NotBlank(message = "챌린지 제목은 필수입니다.")
	@Size(max = 14, message = "챌린지 제목은 14자 이하여야 합니다.")
	String title,

	@Schema(
		description = "챌린지 설명",
		example = "배달음식 줄이고 같이 절약해봐요!",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED,
		nullable = true
	)
	@Size(max = 80, message = "챌린지 설명은 80자 이하여야 합니다.")
	String description,

	@Schema(
		description = "팀 유형 (FRIEND | COUPLE | FAMILY | CLUB | OTHER)",
		example = "FRIEND",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	@NotNull(message = "팀 유형은 필수입니다.")
	TeamType teamType,

	@Schema(
		description = "최대 참여 인원 (2~8명)",
		example = "5",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	@NotNull(message = "최대 참여 인원은 필수입니다.")
	@Min(value = 2, message = "최대 참여 인원은 최소 2명 이상이어야 합니다.")
	@Max(value = 8, message = "최대 참여 인원은 최대 8명까지 가능합니다.")
	Integer maxMemberCount,

	@Schema(
		description = "챌린지 시작 날짜",
		example = "2026-04-05",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	@NotNull(message = "챌린지 시작 날짜는 필수입니다.")
	LocalDate startDate,

	@Schema(
		description = "카테고리 목록 (1~3개)",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	@NotEmpty(message = "카테고리는 최소 1개 이상 선택해야 합니다.")
	@Size(min = 1, max = 3, message = "카테고리는 1개에서 3개까지 선택 가능합니다.")
	@Valid
	List<ChallengeCategoryRequest> categories,

	@Schema(
		description = "팀 전체 목표 절약 금액 (최대 3,000,000원)",
		example = "300000",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	@NotNull(message = "팀 전체 목표 절약 금액은 필수입니다.")
	@Min(value = 0, message = "목표 절약 금액은 0원 이상이어야 합니다.")
	@Max(value = 3_000_000, message = "목표 절약 금액은 3,000,000원 이하여야 합니다.")
	Integer goalAmount,

	@Schema(
		description = "인당 최소 목표 절약 금액 (5,000 ~ 300,000원)",
		example = "50000",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	@NotNull(message = "인당 최소 목표 절약 금액은 필수입니다.")
	@Min(value = 5_000, message = "인당 최소 목표 절약 금액은 5,000원 이상이어야 합니다.")
	@Max(value = 300_000, message = "인당 최소 목표 절약 금액은 300,000원 이하여야 합니다.")
	Integer minPersonalGoalAmount
) {
}
