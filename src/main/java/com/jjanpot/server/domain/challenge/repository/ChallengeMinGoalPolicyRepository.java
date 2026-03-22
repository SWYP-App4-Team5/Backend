package com.jjanpot.server.domain.challenge.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jjanpot.server.domain.challenge.entity.ChallengeMinGoalPolicy;

public interface ChallengeMinGoalPolicyRepository extends JpaRepository<ChallengeMinGoalPolicy, Long> {

	Optional<ChallengeMinGoalPolicy> findByMemberCount(int memberCount);
}
