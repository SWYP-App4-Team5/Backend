package com.jjanpot.server.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjanpot.server.domain.team.entity.Team;
import com.jjanpot.server.domain.team.entity.TeamMembers;
import com.jjanpot.server.domain.team.repository.TeamMembersRepository;
import com.jjanpot.server.domain.team.repository.TeamRepository;
import com.jjanpot.server.domain.user.dto.InviteCodeResponse;
import com.jjanpot.server.domain.user.dto.ProfileCreateRequest;
import com.jjanpot.server.domain.user.dto.ProfileCreateResponse;
import com.jjanpot.server.domain.user.entity.User;
import com.jjanpot.server.domain.user.repository.UserRepository;
import com.jjanpot.server.global.exception.BusinessException;
import com.jjanpot.server.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {

	private final UserRepository userRepository;
	private final TeamRepository teamRepository;
	private final TeamMembersRepository teamMembersRepository;

	@Transactional
	public ProfileCreateResponse onboardingCreateProfile(ProfileCreateRequest request, Long userId) {
		User user = getUserByUserId(userId);

		user.updateOnboarding(
			request.profileImageUrl(),
			request.nickname(),
			request.birthDate()
		);

		return ProfileCreateResponse.of(
			user.getProfileImageUrl(),
			user.getNickname(),
			user.getBirthDate()
		);
	}

	//초대코드 기반 팀 참여 기능 구현
	@Transactional
	public InviteCodeResponse joinChallengeByInviteCode(String inviteCode, Long userId) {
		User user = getUserByUserId(userId);

		Team team = teamRepository.findByInviteCode(inviteCode)
			.orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));

		if (team.getCurrentMemberCount() >= team.getMaxMemberCount()) {
			throw new BusinessException(ErrorCode.TEAM_ALREADY_FULL);
		}

		if (teamMembersRepository.existsByTeamAndUser(team, user)) {
			throw new BusinessException(ErrorCode.ALREADY_TEAM_MEMBER);
		}

		teamMembersRepository.save(TeamMembers.ofMember(team, user));
		team.increaseMemberCount();

		return InviteCodeResponse.of(
			inviteCode,
			team.getTeamName()
		);
	}

	private User getUserByUserId(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
	}
}
