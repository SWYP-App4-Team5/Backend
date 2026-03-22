package com.jjanpot.server.domain.challenge.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

// 홈화면 스크롤 시 팀/개인 절약 현황 통계 화면
@Schema(description = "팀/개인 절약 현황 통계 응답")
public record ChallengeStatsResponse(

	@Schema(description = "팀 절약 현황", requiredMode = Schema.RequiredMode.REQUIRED)
	TeamStats team,

	@Schema(description = "개인 절약 현황", requiredMode = Schema.RequiredMode.REQUIRED)
	PersonalStats personal
) {

	// 팀 절약 현황 (인증평균, 참여율, 연속활동)
	@Schema(description = "팀 절약 현황")
	public record TeamStats(

		@Schema(description = "팀 인증 평균 (회)", example = "2.3", requiredMode = Schema.RequiredMode.REQUIRED)
		double avgCertificationCount,

		@Schema(description = "팀 참여율 (0~100%)", example = "87", requiredMode = Schema.RequiredMode.REQUIRED)
		int participationRate,

		@Schema(description = "팀 연속활동 (일)", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
		int consecutiveDays
	) {
	}

	// 개인 절약 현황 (인증횟수, 참여율, 연속활동)
	@Schema(description = "개인 절약 현황")
	public record PersonalStats(

		@Schema(description = "개인 인증 횟수 (회)", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
		int certificationCount,

		@Schema(description = "개인 참여율 (0~100%)", example = "90", requiredMode = Schema.RequiredMode.REQUIRED)
		int participationRate,

		@Schema(description = "개인 연속활동 (일)", example = "4", requiredMode = Schema.RequiredMode.REQUIRED)
		int consecutiveDays
	) {
	}
}
