package com.jjanpot.server.domain.challenge.dto;

public class ChallengeStatsDto {

	private final long successCount;
	private final long failCount;

	public ChallengeStatsDto(Long successCount, Long failCount) {
		this.successCount = successCount != null ? successCount : 0L;
		this.failCount = failCount != null ? failCount : 0L;
	}

	public long getSuccessCount() {
		return successCount;
	}

	public long getFailCount() {
		return failCount;
	}
}
