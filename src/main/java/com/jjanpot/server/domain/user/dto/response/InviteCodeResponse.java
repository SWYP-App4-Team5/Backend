package com.jjanpot.server.domain.user.dto.response;

import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.team.entity.Team;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "초대코드 팀 참여 응답")
public record InviteCodeResponse(

	@Schema(description = "팀 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	Long teamId,

	@Schema(description = "팀 이름", example = "절약왕팀", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
	String teamName,

	@Schema(description = "팀 유형 (FRIEND | COUPLE | FAMILY | CLUB | OTHER)", example = "FRIEND", requiredMode = Schema.RequiredMode.REQUIRED)
	String teamType,

	@Schema(description = "현재 참여 인원", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
	int currentMemberCount,

	@Schema(description = "최대 참여 인원", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
	int maxMemberCount,

	@Schema(description = "참여한 챌린지 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	Long challengeId
) {
	public static InviteCodeResponse from(Team team, Challenge challenge) {
		return new InviteCodeResponse(
			team.getTeamId(),
			team.getTeamName(),
			team.getType().name(),
			team.getCurrentMemberCount(),
			team.getMaxMemberCount(),
			challenge.getChallengeId()
		);
	}
}
