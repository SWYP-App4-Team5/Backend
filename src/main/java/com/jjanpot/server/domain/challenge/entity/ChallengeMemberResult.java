package com.jjanpot.server.domain.challenge.entity;

import java.math.BigDecimal;

import com.jjanpot.server.domain.user.entity.User;
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
	name = "challenge_member_result",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "UQ_member_result",
			columnNames = {"challenge_id", "user_id"}
		)
	}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChallengeMemberResult extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "result_id")
	private Long resultId;

	@Column(name = "total_saved_amount", nullable = false)
	private Long totalSavedAmount;

	@Column(name = "cert_count", nullable = false)
	private Integer certCount;

	@Column(name = "is_personal_success", nullable = false)
	private Boolean isPersonalSuccess;

	@Column(name = "is_rule_violated", nullable = false)
	@Builder.Default
	private Boolean isRuleViolated = false;

	@Column(name = "streak_days", nullable = false)
	@Builder.Default
	private Integer streakDays = 0;

	@Column(name = "weekly_participation_rate",
		nullable = false, precision = 5, scale = 2)
	@Builder.Default
	private BigDecimal weeklyParticipationRate = BigDecimal.ZERO;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "challenge_id", nullable = false)
	private Challenge challenge;
}