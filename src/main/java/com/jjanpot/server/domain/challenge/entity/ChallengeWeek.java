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
	@Comment("실제 절약 금액")
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

	public void addSavedAmount(int amount) {
		this.weekSavedAmount += amount;
	}

	public void subtractSavedAmount(int amount) {
		this.weekSavedAmount -= amount;
	}
}