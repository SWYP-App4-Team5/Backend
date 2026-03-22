package com.jjanpot.server.domain.challenge.entity;

import org.hibernate.annotations.Comment;

import com.jjanpot.server.domain.category.entity.Category;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "challenge_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChallengeCategory {

	@EmbeddedId
	private ChallengeCategoryId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("challengeId")
	@JoinColumn(name = "challenge_id", nullable = false)
	private Challenge challenge;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("categoryId")
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	@Column(name = "amount", nullable = false)
	@Comment("챌린지 생성 시 설정한 해당 카테고리의 기준 금액")
	private int amount;

	public static ChallengeCategory of(Challenge challenge, Category category, int amount) {
		return ChallengeCategory.builder()
			.id(new ChallengeCategoryId(challenge.getChallengeId(), category.getCategoryId()))
			.challenge(challenge)
			.category(category)
			.amount(amount)
			.build();
	}
}