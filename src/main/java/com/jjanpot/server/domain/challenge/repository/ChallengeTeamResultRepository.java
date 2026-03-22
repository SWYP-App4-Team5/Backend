package com.jjanpot.server.domain.challenge.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jjanpot.server.domain.challenge.entity.ChallengeTeamResult;

public interface ChallengeTeamResultRepository extends JpaRepository<ChallengeTeamResult, Long> {
}
