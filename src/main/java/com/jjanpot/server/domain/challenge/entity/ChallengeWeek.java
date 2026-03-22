package com.jjanpot.server.domain.challenge.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;

import com.jjanpot.server.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "challenge_week",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "UQ_challenge_week",
			columnNames = {"challenge_id", "week_number"}
		)
	}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChallengeWeek extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "week_id")
	private Long weekId;

	@Column(name = "week_number", nullable = false)
	@Comment("주차 숫자")
	private int weekNumber;

	@Column(name = "week_goal_amount", nullable = false)
	@Comment("이번주 목표 금액")
	private int weekGoalAmount;

	@Column(name = "week_saved_amount", nullable = false)
	@Builder.Default
	@Comment("실제 절약 금액 (팀 전체의 누적 절약 금액)")
	private int weekSavedAmount = 0;

	@Column(name = "start_date", nullable = false)
	@Comment("시작 일시")
	private LocalDateTime startDate;

	@Column(name = "end_date", nullable = false)
	@Comment("종료 일시")
	private LocalDateTime endDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "challenge_id", nullable = false)
	private Challenge challenge;

	public static ChallengeWeek firstWeek(
		Challenge challenge,
		LocalDateTime startDateTime,
		LocalDateTime endDateTime
	) {
		return ChallengeWeek.builder()
			.weekNumber(1)
			.weekGoalAmount(challenge.getGoalAmount())
			.startDate(startDateTime)
			.endDate(endDateTime)
			.challenge(challenge)
			.build();
	}

	// 인증 생성 시 활용 (saved_amount는 기준 초과 소비 시 음수 가능)
	public void addSavedAmount(int amount) {
		this.weekSavedAmount += amount;
	}

	// 인증 삭제 시 활용 (이전에 더한 saved_amount를 역산)
	public void subtractSavedAmount(int amount) {
		this.weekSavedAmount -= amount;
	}
}

/*
 weekSavedAmount는 팀 전체의 누적 절약 금액이고,
 이 값을 weekGoalAmount와 비교해서 챌린지 달성 여부를 판단하는 데 사용됨
*/

/* 인증 예시
	팀 목표 절약 금액: 200,000원
	이번 주 weekSavedAmount: 0원
	→ 유저A가 외식비 30,000원 절약 등록
	addSavedAmount(30,000) → weekSavedAmount = 30,000원
	→ 유저B가 교통비 15,000원 절약 등록
	addSavedAmount(15,000) → weekSavedAmount = 45,000원
	→ 유저A가 기록 잘못 입력해서 삭제
	subtractSavedAmount(30,000) → weekSavedAmount = 15,000원
*/