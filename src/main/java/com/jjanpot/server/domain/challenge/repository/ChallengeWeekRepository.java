package com.jjanpot.server.domain.challenge.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.challenge.entity.ChallengeWeek;

public interface ChallengeWeekRepository extends JpaRepository<ChallengeWeek, Long> {

	List<ChallengeWeek> findAllByChallenge(Challenge challenge);

	Optional<ChallengeWeek> findByChallengeAndWeekNumber(Challenge challenge, Integer weekNumber);
}
