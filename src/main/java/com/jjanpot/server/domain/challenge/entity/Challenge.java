package com.jjanpot.server.domain.challenge.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;

import com.jjanpot.server.domain.challenge.dto.request.CreateChallengeRequest;
import com.jjanpot.server.domain.team.entity.Team;
import com.jjanpot.server.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "challenge")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Challenge extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "challenge_id")
	private Long challengeId;

	@Column(name = "title", nullable = false, length = 100)
	@Comment("챌린지 제목")
	private String title;

	@Column(name = "description", length = 255)
	@Comment("챌린지 설명")
	private String description;

	@Column(name = "goal_amount", nullable = false)
	@Comment("팀 전체 목표 절약 금액")
	private int goalAmount;

	@Column(name = "min_personal_goal_amount", nullable = false)
	@Comment("인당 최소 목표 절약 금액")
	private int minPersonalGoalAmount;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	@Comment("챌린지 상태")
	private ChallengeStatus status;

	@Column(name = "start_date", nullable = false)
	@Comment("시작 일시")
	private LocalDateTime startDate;

	@Column(name = "end_date", nullable = false)
	@Comment("종료 일시")
	private LocalDateTime endDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id", nullable = false)
	private Team team;

	public static Challenge from(
		CreateChallengeRequest request,
		Team team,
		LocalDateTime startDateTime,
		LocalDateTime endDateTime
	) {
		return Challenge.builder()
			.title(request.title())
			.description(request.description())
			.goalAmount(request.goalAmount())
			.minPersonalGoalAmount(request.minPersonalGoalAmount())
			.status(ChallengeStatus.WAITING)
			.startDate(startDateTime)
			.endDate(endDateTime)
			.team(team)
			.build();
	}

	public void updateStatus(ChallengeStatus status) {
		this.status = status;
	}

	public void updateGoalAmount(int goalAmount) {
		this.goalAmount = goalAmount;
	}

	public void updateDates(LocalDateTime startDate, LocalDateTime endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}
}