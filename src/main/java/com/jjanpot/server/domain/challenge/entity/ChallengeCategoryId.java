package com.jjanpot.server.domain.challenge.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChallengeCategoryId implements Serializable {

	@Column(name = "challenge_id")
	private Long challengeId;

	@Column(name = "category_id")
	private Long categoryId;
}