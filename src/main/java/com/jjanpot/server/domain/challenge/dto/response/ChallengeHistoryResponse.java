package com.jjanpot.server.domain.challenge.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jjanpot.server.domain.challenge.entity.Challenge;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "챌린지 이력 응답")
public record ChallengeHistoryResponse(

	@Schema(description = "챌린지 ID", example = "1")
	Long challengeId,

	@Schema(description = "챌린지 제목", example = "카페는 이제 그만!")
	String title,

	@Schema(description = "챌린지 상태 (COMPLETED: 목표 달성 | FAILED: 목표 미달성)", example = "COMPLETED")
	String status,

	@Schema(description = "챌린지 상태 한국어 (목표 달성 성공 챌린지 | 목표 미달성 실패 챌린지)", example = "목표 달성 성공 챌린지")
	String statusDisplayName,

	@Schema(description = "팀 목표 금액", example = "300000")
	int goalAmount,

	@Schema(description = "챌린지 시작일", example = "2026-03-25T00:00:00")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	LocalDateTime startDate,

	@Schema(description = "챌린지 종료일", example = "2026-04-01T00:00:00")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	LocalDateTime endDate
) {

	public static ChallengeHistoryResponse from(Challenge challenge) {
		return new ChallengeHistoryResponse(
			challenge.getChallengeId(),
			challenge.getTitle(),
			challenge.getStatus().name(),
			challenge.getStatus().getDisplayName(),
			challenge.getGoalAmount(),
			challenge.getStartDate(),
			challenge.getEndDate()
		);
	}
}
