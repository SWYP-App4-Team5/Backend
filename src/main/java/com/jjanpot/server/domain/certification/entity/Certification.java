package com.jjanpot.server.domain.certification.entity;

import com.jjanpot.server.domain.category.entity.Category;
import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.challenge.entity.ChallengeWeek;
import com.jjanpot.server.domain.user.entity.User;
import com.jjanpot.server.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "certification", indexes = {
	@Index(name = "idx_certification_created_at", columnList = "created_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Certification extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "certification_id")
	private Long certificationId;

	@Enumerated(EnumType.STRING)
	@Column(name = "spend_type", nullable = false)
	private SpendType spendType;

	@Column(name = "spend_amount", nullable = false)
	private Long spendAmount;

	@Column(name = "saved_amount", nullable = false)
	private Long savedAmount;

	@Column(name = "memo", nullable = false, length = 256)
	private String memo;

	@Column(name = "image_url", columnDefinition = "TEXT")
	private String imageUrl;

	@Column(name = "spent_at", nullable = false)
	private java.time.LocalDateTime spentAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "challenge_id", nullable = false)
	private Challenge challenge;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "week_id", nullable = false)
	private ChallengeWeek challengeWeek;
}