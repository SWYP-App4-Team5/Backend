package com.jjanpot.server.domain.challenge.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChallengeStatus {
	WAITING("대기중인 챌린지"),
	ONGOING("진행중인 챌린지"),
	COMPLETED("목표 달성 후 완료된 챌린지"),
	FAILED("목표 미달성 챌린지"),
	CANCELLED("취소된 챌린지");

	private final String displayName;
}