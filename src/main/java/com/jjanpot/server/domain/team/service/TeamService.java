package com.jjanpot.server.domain.team.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.challenge.entity.ChallengeStatus;
import com.jjanpot.server.domain.challenge.repository.ChallengeRepository;
import com.jjanpot.server.domain.team.dto.JoinTeamRequest;
import com.jjanpot.server.domain.team.dto.JoinTeamResponse;
import com.jjanpot.server.domain.team.entity.Team;
import com.jjanpot.server.domain.team.entity.TeamMembers;
import com.jjanpot.server.domain.team.repository.TeamMembersRepository;
import com.jjanpot.server.domain.team.repository.TeamRepository;
import com.jjanpot.server.domain.user.entity.User;
import com.jjanpot.server.global.exception.BusinessException;
import com.jjanpot.server.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

	private final TeamRepository teamRepository;
	private final TeamMembersRepository teamMembersRepository;
	private final ChallengeRepository challengeRepository;

	/** 초대 코드로 팀 참여 **/
	@Transactional
	public JoinTeamResponse joinTeam(User user, JoinTeamRequest request) {

		// 초대 코드로 팀 조회
		Team team = teamRepository.findByInviteCode(request.inviteCode())
			.orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));

		// 대기 중인 챌린지 조회 (WAITING 상태일 때만 참여 가능)
		Challenge challenge = challengeRepository.findByTeamAndStatus(team, ChallengeStatus.WAITING)
			.orElseThrow(() -> new BusinessException(ErrorCode.CHALLENGE_NOT_JOINABLE));

		// 이미 팀원인지 확인
		if (teamMembersRepository.existsByTeamAndUser(team, user)) {
			throw new BusinessException(ErrorCode.ALREADY_TEAM_MEMBER);
		}

		// 정원 초과 확인
		if (team.getCurrentMemberCount() >= team.getMaxMemberCount()) {
			throw new BusinessException(ErrorCode.TEAM_ALREADY_FULL);
		}

		teamMembersRepository.save(TeamMembers.ofMember(team, user));
		team.increaseMemberCount();

		return JoinTeamResponse.from(team, challenge);
	}
}
