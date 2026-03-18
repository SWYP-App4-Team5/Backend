package com.jjanpot.server.domain.challenge.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.challenge.entity.ChallengeStatus;
import com.jjanpot.server.domain.team.entity.Team;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

	List<Challenge> findAllByTeam(Team team);

	List<Challenge> findAllByStatus(ChallengeStatus status);

	Optional<Challenge> findByTeamAndStatus(Team team, ChallengeStatus status);
}
