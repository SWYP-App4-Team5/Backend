package com.jjanpot.server.domain.challenge.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.challenge.entity.ChallengeCategory;
import com.jjanpot.server.domain.team.entity.Team;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "챌린지 생성 응답")
public record CreateChallengeResponse(

	@Schema(description = "생성된 챌린지 ID", example = "1")
	Long challengeId,

	@Schema(description = "생성된 팀 ID", example = "1")
	Long teamId,

	@Schema(description = "팀 초대코드 (6자리)", example = "AB3K7Z")
	String inviteCode,

	@Schema(description = "팀 전체 목표 절약 금액", example = "200000")
	Integer goalAmount,

	@Schema(description = "인당 최소 목표 절약 금액", example = "30000")
	Integer minPersonalGoalAmount,

	@Schema(description = "챌린지 시작 일시", example = "2026-03-18T00:00:00")
	LocalDateTime startDate,

	@Schema(description = "챌린지 종료 일시", example = "2026-03-25T00:00:00")
	LocalDateTime endDate,

	@Schema(description = "챌린지 카테고리 목록")
	List<CategoryInfo> categories
) {

	@Schema(description = "카테고리 정보")
	public record CategoryInfo(

		@Schema(description = "카테고리 ID", example = "1")
		Long categoryId,

		@Schema(description = "카테고리별 기준 금액", example = "50000")
		int amount
	) {
		public static CategoryInfo from(ChallengeCategory challengeCategory) {
			return new CategoryInfo(
				challengeCategory.getCategory().getCategoryId(),
				challengeCategory.getAmount()
			);
		}
	}

	public static CreateChallengeResponse from(
		Challenge challenge,
		Team team,
		List<ChallengeCategory> challengeCategories
	) {
		return new CreateChallengeResponse(
			challenge.getChallengeId(),
			team.getTeamId(),
			team.getInviteCode(),
			challenge.getGoalAmount(),
			challenge.getMinPersonalGoalAmount(),
			challenge.getStartDate(),
			challenge.getEndDate(),
			challengeCategories.stream()
				.map(CategoryInfo::from)
				.toList()
		);
	}
}
