package com.jjanpot.server.domain.team.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jjanpot.server.domain.team.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {

	boolean existsByInviteCode(String inviteCode);

	Optional<Team> findByInviteCode(String inviteCode);
}
