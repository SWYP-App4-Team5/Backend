package com.jjanpot.server.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.challenge.entity.ChallengeStatus;
import com.jjanpot.server.domain.challenge.repository.ChallengeRepository;
import com.jjanpot.server.domain.team.entity.Team;
import com.jjanpot.server.domain.team.entity.TeamMembers;
import com.jjanpot.server.domain.team.repository.TeamMembersRepository;
import com.jjanpot.server.domain.team.repository.TeamRepository;
import com.jjanpot.server.domain.user.dto.request.ProfileCreateRequest;
import com.jjanpot.server.domain.user.dto.request.UserAgreementRequest;
import com.jjanpot.server.domain.user.dto.response.InviteCodeResponse;
import com.jjanpot.server.domain.user.dto.response.ProfileCreateResponse;
import com.jjanpot.server.domain.user.entity.User;
import com.jjanpot.server.domain.user.entity.UserAgreement;
import com.jjanpot.server.domain.user.repository.UserAgreementRepository;
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
	private final ChallengeRepository challengeRepository;
	private final UserAgreementRepository userAgreementRepository;

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

	// 초대코드 기반 팀 참여 (온보딩 흐름)
	@Transactional
	public InviteCodeResponse joinChallengeByInviteCode(String inviteCode, Long userId) {
		User user = getUserByUserId(userId);

		// 1. 초대코드로 팀 조회
		Team team = teamRepository.findByInviteCode(inviteCode)
			.orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));

		// 2. 해당 팀의 WAITING 챌린지 조회 (시작 전에만 참여 가능)
		Challenge challenge = challengeRepository.findByTeamAndStatus(team, ChallengeStatus.WAITING)
			.orElseThrow(() -> new BusinessException(ErrorCode.CHALLENGE_NOT_JOINABLE));

		// 3. 이미 팀원인지 확인
		if (teamMembersRepository.existsByTeamAndUser(team, user)) {
			throw new BusinessException(ErrorCode.ALREADY_TEAM_MEMBER);
		}

		// 4. 정원 초과 확인
		if (team.getCurrentMemberCount() >= team.getMaxMemberCount()) {
			throw new BusinessException(ErrorCode.TEAM_ALREADY_FULL);
		}

		// 5. 팀원으로 등록
		teamMembersRepository.save(TeamMembers.ofMember(team, user));

		// 6. 팀의 현재 인원 증가
		team.increaseMemberCount();

		return InviteCodeResponse.from(team, challenge);
	}

	//약관 동의
	@Transactional
	public void agreeToTerms(Long userId, UserAgreementRequest request) {
		User user = getUserByUserId(userId);

		if (!Boolean.TRUE.equals(request.ageVerified())
			|| !Boolean.TRUE.equals(request.termsOfServiceAgreed())
			|| !Boolean.TRUE.equals(request.privacyPolicyAgreed())) {
			throw new BusinessException(ErrorCode.REQUIRED_AGREEMENT_MISSING);
		}

		//약관 동의 여부 확인
		if (userAgreementRepository.existsByUser(user)) {
			throw new BusinessException(ErrorCode.ALREADY_AGREED_TERMS);
		}

		userAgreementRepository.save(UserAgreement.from(
			request.ageVerified(),
			request.termsOfServiceAgreed(),
			request.privacyPolicyAgreed(),
			Boolean.TRUE.equals(request.marketingConsent()),
			user
		));
	}

	private User getUserByUserId(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
	}
}
