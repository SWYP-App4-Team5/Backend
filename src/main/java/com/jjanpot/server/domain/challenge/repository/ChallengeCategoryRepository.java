package com.jjanpot.server.domain.challenge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.challenge.entity.ChallengeCategory;
import com.jjanpot.server.domain.challenge.entity.ChallengeCategoryId;

public interface ChallengeCategoryRepository extends JpaRepository<ChallengeCategory, ChallengeCategoryId> {

	List<ChallengeCategory> findAllByChallenge(Challenge challenge);
}
