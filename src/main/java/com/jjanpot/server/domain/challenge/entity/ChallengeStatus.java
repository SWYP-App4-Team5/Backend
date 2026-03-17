package com.jjanpot.server.domain.challenge.entity;

public enum ChallengeStatus {
    WAITING,    // 시작일 전 대기
    ONGOING,    // 진행중
    COMPLETED,  // 목표 달성 종료
    FAILED      // 목표 미달성 종료
}