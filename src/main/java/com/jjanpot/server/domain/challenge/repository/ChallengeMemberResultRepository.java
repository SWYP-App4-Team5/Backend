package com.jjanpot.server.domain.challenge.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jjanpot.server.domain.challenge.dto.ChallengeStatsDto;
import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.challenge.entity.ChallengeMemberResult;
import com.jjanpot.server.domain.challenge.entity.ChallengeStatus;
import com.jjanpot.server.domain.user.entity.User;

public interface ChallengeMemberResultRepository extends JpaRepository<ChallengeMemberResult, Long> {

	Optional<ChallengeMemberResult> findByChallengeAndUser(Challenge challenge, User user);

	void deleteAllByUser(User user);

	@Query("SELECT new com.jjanpot.server.domain.challenge.dto.ChallengeStatsDto("
		+ "SUM(CASE WHEN r.challenge.status = :completed THEN 1L ELSE 0L END), "
		+ "SUM(CASE WHEN r.challenge.status = :failed THEN 1L ELSE 0L END)) "
		+ "FROM ChallengeMemberResult r "
		+ "WHERE r.user = :user")
	ChallengeStatsDto aggregateChallengeStatsByUser(
		@Param("user") User user,
		@Param("completed") ChallengeStatus completed,
		@Param("failed") ChallengeStatus failed
	);
}
