package com.jjanpot.server.domain.challenge.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jjanpot.server.domain.challenge.entity.ChallengeMemberResult;

public interface ChallengeMemberResultRepository extends JpaRepository<ChallengeMemberResult, Long> {
}
