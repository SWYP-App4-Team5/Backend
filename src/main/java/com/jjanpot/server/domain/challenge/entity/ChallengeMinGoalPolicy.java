package com.jjanpot.server.domain.challenge.entity;

import com.jjanpot.server.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "challenge_min_goal_policy")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
// 정책 테이블(인원수별 최소 목표 금액 기준)
public class ChallengeMinGoalPolicy extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "policy_id")
	private Long policyId;

	@Column(name = "member_count", nullable = false, unique = true)
	@Comment("인원 수")
	private Integer memberCount;

	@Column(name = "min_amount", nullable = false)
	@Comment("최소 목표 금액")
	private Long minAmount;
}
