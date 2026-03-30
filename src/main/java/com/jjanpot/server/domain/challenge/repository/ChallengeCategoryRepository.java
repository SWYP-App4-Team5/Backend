package com.jjanpot.server.domain.challenge.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.challenge.entity.ChallengeCategory;
import com.jjanpot.server.domain.challenge.entity.ChallengeCategoryId;

public interface ChallengeCategoryRepository extends JpaRepository<ChallengeCategory, ChallengeCategoryId> {

	List<ChallengeCategory> findAllByChallenge(Challenge challenge);

	/** 챌린지 + 카테고리 ID로 단건 조회 (인증 생성 시 유효성 검증용) */
	Optional<ChallengeCategory> findByChallengeAndCategory_CategoryId(Challenge challenge, Long categoryId);
}
