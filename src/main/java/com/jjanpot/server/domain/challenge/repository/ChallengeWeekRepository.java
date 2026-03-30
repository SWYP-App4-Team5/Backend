package com.jjanpot.server.domain.challenge.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.challenge.entity.ChallengeWeek;

import jakarta.persistence.LockModeType;

public interface ChallengeWeekRepository extends JpaRepository<ChallengeWeek, Long> {

	List<ChallengeWeek> findAllByChallenge(Challenge challenge);

	Optional<ChallengeWeek> findByChallengeAndWeekNumber(Challenge challenge, Integer weekNumber);

	/** 비관적 잠금으로 ChallengeWeek 조회 (동시 인증 시 weekSavedAmount 경쟁 갱신 방지) */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT cw FROM ChallengeWeek cw WHERE cw.challenge = :challenge AND cw.weekNumber = :weekNumber")
	Optional<ChallengeWeek> findByChallengeAndWeekNumberForUpdate(
		@Param("challenge") Challenge challenge,
		@Param("weekNumber") Integer weekNumber
	);
}
