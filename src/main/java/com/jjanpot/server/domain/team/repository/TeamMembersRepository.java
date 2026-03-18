package com.jjanpot.server.domain.team.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jjanpot.server.domain.team.entity.Team;
import com.jjanpot.server.domain.team.entity.TeamMembers;
import com.jjanpot.server.domain.team.entity.TeamMembersId;
import com.jjanpot.server.domain.user.entity.User;

public interface TeamMembersRepository extends JpaRepository<TeamMembers, TeamMembersId> {

	List<TeamMembers> findAllByTeam(Team team);

	Optional<TeamMembers> findByTeamAndUser(Team team, User user);

	boolean existsByTeamAndUser(Team team, User user);
}
