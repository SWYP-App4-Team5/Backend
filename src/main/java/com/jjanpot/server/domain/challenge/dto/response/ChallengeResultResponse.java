package com.jjanpot.server.domain.challenge.dto.response;

import java.util.List;

import com.jjanpot.server.domain.challenge.entity.ChallengeCategory;
import com.jjanpot.server.domain.challenge.entity.ChallengeMemberResult;
import com.jjanpot.server.domain.challenge.entity.ChallengeTeamResult;

public record ChallengeResultResponse(
	boolean isTeamSuccess,
	int goalAmount,
	int totalSavedAmount,
	int achievementRate,
	List<String> categoryNames,
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
