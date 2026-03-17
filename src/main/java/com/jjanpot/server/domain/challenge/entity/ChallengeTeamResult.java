package com.jjanpot.server.domain.challenge.entity;

import java.math.BigDecimal;

import com.jjanpot.server.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "challenge_team_result",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "UQ_team_result",
			columnNames = {"challenge_id"}
		)
	}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChallengeTeamResult extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "result_id")
	private Long resultId;

	@Column(name = "goal_amount", nullable = false)
	private Long goalAmount;

	@Column(name = "total_saved_amount", nullable = false)
	private Long totalSavedAmount;

	@Column(name = "total_cert_count", nullable = false)
	private Integer totalCertCount;

	@Column(name = "is_team_success", nullable = false)
	private Boolean isTeamSuccess;

	@Column(name = "team_streak_days", nullable = false)
	@Builder.Default
	private Integer teamStreakDays = 0;

	@Column(name = "achievement_rate", nullable = false,
		precision = 5, scale = 2)
	@Builder.Default
	private BigDecimal achievementRate = BigDecimal.ZERO;

	@Column(name = "avg_weekly_cert_count", nullable = false,
		precision = 5, scale = 2)
	@Builder.Default
	private BigDecimal avgWeeklyCertCount = BigDecimal.ZERO;

	@Column(name = "avg_weekly_participation_rate",
		nullable = false, precision = 5, scale = 2)
	@Builder.Default
	private BigDecimal avgWeeklyParticipationRate = BigDecimal.ZERO;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "challenge_id", nullable = false)
	private Challenge challenge;
}