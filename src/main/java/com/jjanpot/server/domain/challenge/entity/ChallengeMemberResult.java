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
import org.hibernate.annotations.Comment;

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
	@Comment("개인 합산 절약 금액")
	private Long totalSavedAmount;

	@Column(name = "total_cert_count", nullable = false)
	@Comment("개인 총 인증 횟수")
	private Integer totalCertCount;

	@Column(name = "is_personal_success", nullable = false)
	@Comment("개인 최소 금액 충족 여부")
	private Boolean isPersonalSuccess;

	@Column(name = "is_rule_violated", nullable = false)
	@Builder.Default
	@Comment("규칙 위반 여부")
	private Boolean isRuleViolated = false;

	@Column(name = "streak_days", nullable = false)
	@Builder.Default
	@Comment("연속 활동일")
	private Integer streakDays = 0;

	@Column(name = "weekly_participation_rate",
		nullable = false, precision = 5, scale = 2)
	@Builder.Default
	@Comment("주간 참여율 (%)")
	private BigDecimal weeklyParticipationRate = BigDecimal.ZERO;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "challenge_id", nullable = false)
	private Challenge challenge;
}