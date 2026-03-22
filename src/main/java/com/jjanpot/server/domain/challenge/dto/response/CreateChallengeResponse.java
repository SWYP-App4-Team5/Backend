package com.jjanpot.server.domain.challenge.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.challenge.entity.ChallengeCategory;
import com.jjanpot.server.domain.team.entity.Team;

public record CreateChallengeResponse(
	Long challengeId,
	Long teamId,
	//String teamName,
	String inviteCode,
	Integer goalAmount,
	Integer minPersonalGoalAmount,
	LocalDateTime startDate,
	LocalDateTime endDate,
	List<CategoryInfo> categories
) {

	public record CategoryInfo(
		Long categoryId,
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
			//team.getTeamName(),
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
