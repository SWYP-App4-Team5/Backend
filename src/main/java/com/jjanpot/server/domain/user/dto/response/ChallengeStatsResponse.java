package com.jjanpot.server.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "마이페이지 챌린지 통계 조회 응답")
public record ChallengeStatsResponse(

	@Schema(description = "총 챌린지 수", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
	long totalCount,

	@Schema(description = "성공 수 (COMPLETED)", example = "7", requiredMode = Schema.RequiredMode.REQUIRED)
	long successCount,

	@Schema(description = "실패 수 (FAILED)", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
	long failCount,

	@Schema(description = "성공률 (%)", example = "70", requiredMode = Schema.RequiredMode.REQUIRED)
	int successRate
) {
	public static ChallengeStatsResponse of(long successCount, long failCount) {
		long totalCount = successCount + failCount;
		int successRate = totalCount > 0 ? (int) (successCount * 100 / totalCount) : 0;
		return new ChallengeStatsResponse(totalCount, successCount, failCount, successRate);
	}
}
