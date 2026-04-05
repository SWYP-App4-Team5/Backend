package com.jjanpot.server.domain.team.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jjanpot.server.domain.team.entity.Team;
import com.jjanpot.server.domain.team.entity.TeamMembers;
import com.jjanpot.server.domain.team.entity.TeamMembersId;
import com.jjanpot.server.domain.user.entity.User;

public interface TeamMembersRepository extends JpaRepository<TeamMembers, TeamMembersId> {

	/** 특정 팀의 전체 팀원 목록 조회 */
	// 팀원 목록 화면, 팀원 수 확인 등에 활용
	List<TeamMembers> findAllByTeam(Team team);

	/** 특정 유저가 속한 모든 팀 조회 */
	// getCurrentChallenge()에서 유저의 활성 챌린지 찾을 때 활용
	List<TeamMembers> findAllByUser(User user);

	/** 특정 팀에서 특정 유저의 멤버십 조회 */
	// 팀장 여부 확인(cancelChallenge, getChallengeDetail)
	// 챌린지 접근 권한 확인에 활용
	Optional<TeamMembers> findByTeamAndUser(Team team, User user);

	/** 특정 팀에 특정 유저가 이미 속해 있는지 확인 */
	// joinTeam()에서 중복 참여 방지에 활용
	boolean existsByTeamAndUser(Team team, User user);

	/** 유저의 팀 멤버십 일괄 삭제 (회원 탈퇴용) */
	void deleteAllByUser(User user);
}
