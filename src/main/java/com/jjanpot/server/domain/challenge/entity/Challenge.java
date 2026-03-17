package com.jjanpot.server.domain.challenge.entity;

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
	private String title;

	@Column(name = "memo", length = 100)
	private String memo;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private ChallengeType type;

	@Column(name = "goal_amount", nullable = false)
	private Long goalAmount;

	@Column(name = "min_personal_goal_amount", nullable = false)
	private Long minPersonalGoalAmount;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private ChallengeStatus status;

	@Column(name = "start_date", nullable = false)
	private java.time.LocalDateTime startDate;

	@Column(name = "end_date", nullable = false)
	private java.time.LocalDateTime endDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id", nullable = false)
	private Team team;

	public void updateStatus(ChallengeStatus status) {
		this.status = status;
	}

	public void updateGoalAmount(Long goalAmount) {
		this.goalAmount = goalAmount;
	}
}