package com.jjanpot.server.domain.team.dto;

import java.time.LocalDateTime;

import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.team.entity.Team;

public record JoinTeamResponse(
	Long teamId,
	String teamName,
	int currentMemberCount,
	int maxMemberCount,
	Long challengeId,
	LocalDateTime startDate,
	LocalDateTime endDate
) {
	public static JoinTeamResponse from(Team team, Challenge challenge) {
		return new JoinTeamResponse(
			team.getTeamId(),
			team.getTeamName(),
			team.getCurrentMemberCount(),
			team.getMaxMemberCount(),
			challenge.getChallengeId(),
			challenge.getStartDate(),
			challenge.getEndDate()
		);
	}
}
