package com.jjanpot.server.domain.challenge.entity;

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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

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
	private Integer weekNumber;

	@Column(name = "week_goal_amount", nullable = false)
	@Comment("이번주 목표 금액")
	private Long weekGoalAmount;

	@Column(name = "week_saved_amount", nullable = false)
	@Builder.Default
	@Comment("실제 절약 금액")
	private Long weekSavedAmount = 0L;

	@Column(name = "start_date", nullable = false)
	@Comment("시작 일시")
	private LocalDateTime startDate;

	@Column(name = "end_date", nullable = false)
	@Comment("종료 일시")
	private LocalDateTime endDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "challenge_id", nullable = false)
	private Challenge challenge;

	public void addSavedAmount(Long amount) {
		this.weekSavedAmount += amount;
	}

	public void subtractSavedAmount(Long amount) {
		this.weekSavedAmount -= amount;
	}
}