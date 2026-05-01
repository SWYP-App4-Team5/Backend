package com.jjanpot.server.domain.challenge.dto.response;

import java.util.List;

import com.jjanpot.server.domain.challenge.entity.ChallengeCategory;
import com.jjanpot.server.domain.challenge.entity.ChallengeMemberResult;
import com.jjanpot.server.domain.challenge.entity.ChallengeTeamResult;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "챌린지 결과 조회 응답")
public record ChallengeResultResponse(

	@Schema(description = "팀 챌린지 성공 여부 - true면 성공 화면, false면 실패 화면 노출", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
	boolean isTeamSuccess,

	@Schema(description = "팀 전체 목표 절약 금액 (원)", example = "300000", requiredMode = Schema.RequiredMode.REQUIRED)
	int goalAmount,

	@Schema(description = "팀 전체 실제 절약 금액 (원)", example = "270000", requiredMode = Schema.RequiredMode.REQUIRED)
	int totalSavedAmount,

	@Schema(description = "팀 목표 달성률 (%)", example = "90", requiredMode = Schema.RequiredMode.REQUIRED)
	int achievementRate,

	@Schema(description = "챌린지 카테고리 이름 목록 (1~3개)", example = "[\"배달/외식\", \"카페/디저트\"]", requiredMode = Schema.RequiredMode.REQUIRED)
	List<String> categoryNames,

	@Schema(description = "내 개인 절약 금액 (원)", example = "85000", requiredMode = Schema.RequiredMode.REQUIRED)
	long personalSavedAmount

) {

	public static ChallengeResultResponse from(
		ChallengeTeamResult teamResult,
		ChallengeMemberResult memberResult,
		List<ChallengeCategory> categories
	) {
		List<String> categoryNames = categories.stream()
			.map(cc -> cc.getCategory().getName().getDisplayName())
			.toList();

		return new ChallengeResultResponse(
			teamResult.isTeamSuccess(),
			teamResult.getGoalAmount(),
			teamResult.getTotalSavedAmount(),
			teamResult.getAchievementRate().intValue(),
			categoryNames,
			memberResult.getTotalSavedAmount()
		);
	}
}
