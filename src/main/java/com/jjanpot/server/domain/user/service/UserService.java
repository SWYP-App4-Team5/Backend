package com.jjanpot.server.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.challenge.entity.ChallengeStatus;
import com.jjanpot.server.domain.challenge.dto.ChallengeStatsDto;
import com.jjanpot.server.domain.challenge.repository.ChallengeMemberResultRepository;
import com.jjanpot.server.domain.challenge.repository.ChallengeRepository;
import com.jjanpot.server.domain.team.entity.Team;
import com.jjanpot.server.domain.team.entity.TeamMembers;
import com.jjanpot.server.domain.team.repository.TeamMembersRepository;
import com.jjanpot.server.domain.team.repository.TeamRepository;
import com.jjanpot.server.domain.user.dto.request.NotificationUpdateRequest;
import com.jjanpot.server.domain.user.dto.request.ProfileCreateRequest;
import com.jjanpot.server.domain.user.dto.request.UserAgreementRequest;
import com.jjanpot.server.domain.user.dto.response.ChallengeStatsResponse;
import com.jjanpot.server.domain.user.dto.response.InviteCodeResponse;
import com.jjanpot.server.domain.user.dto.response.NotificationResponse;
import com.jjanpot.server.domain.user.dto.response.ProfileCreateResponse;
import com.jjanpot.server.domain.user.entity.User;
import com.jjanpot.server.domain.user.entity.UserAgreement;
import com.jjanpot.server.domain.user.repository.UserAgreementRepository;
import com.jjanpot.server.domain.user.repository.UserRepository;
import com.jjanpot.server.global.common.service.ImageUploadService;
import com.jjanpot.server.global.exception.BusinessException;
import com.jjanpot.server.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {

	private static final String PROFILE_FILE_NAME = "profile/";
	private static final String DEFAULT_PROFILE_IMAGE = "https://jjanpot-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/profile/defaultImg.png";

	private final UserRepository userRepository;
	private final TeamRepository teamRepository;
	private final TeamMembersRepository teamMembersRepository;
	private final ChallengeRepository challengeRepository;
	private final ChallengeMemberResultRepository challengeMemberResultRepository;
	private final UserAgreementRepository userAgreementRepository;
	private final ImageUploadService imageUploadService;

	//프로필 생성
	@Transactional
	public ProfileCreateResponse onboardingCreateProfile(ProfileCreateRequest request,
		MultipartFile image, Long userId) {
		User user = getUserByUserId(userId);

		String imageUrl = imageUploadService.upload(image, PROFILE_FILE_NAME);

		if (imageUrl == null) {
			imageUrl = DEFAULT_PROFILE_IMAGE;
		}

		user.updateOnboarding(
			imageUrl,
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

		Team team = teamRepository.findByInviteCode(inviteCode)
			.orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));
		Challenge challenge = challengeRepository.findByTeamAndStatus(team, ChallengeStatus.WAITING)
			.orElseThrow(() -> new BusinessException(ErrorCode.CHALLENGE_NOT_JOINABLE));
		if (teamMembersRepository.existsByTeamAndUser(team, user)) {
			throw new BusinessException(ErrorCode.ALREADY_TEAM_MEMBER);
		}
		if (team.getCurrentMemberCount() >= team.getMaxMemberCount()) {
			throw new BusinessException(ErrorCode.TEAM_ALREADY_FULL);
		}
		teamMembersRepository.save(TeamMembers.ofMember(team, user));
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

	public ChallengeStatsResponse getChallengeStats(Long userId) {
		User user = getUserByUserId(userId);
		ChallengeStatsDto dto = challengeMemberResultRepository.aggregateChallengeStatsByUser(
			user, ChallengeStatus.COMPLETED, ChallengeStatus.FAILED);
		return ChallengeStatsResponse.of(dto.getSuccessCount(), dto.getFailCount());
	}

	public NotificationResponse getNotification(Long userId) {
		User user = getUserByUserId(userId);
		UserAgreement agreement = userAgreementRepository.findByUser(user)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_AGREEMENT_NOT_FOUND));
		return NotificationResponse.of(user, agreement);
	}

	@Transactional
	public void updateNotification(Long userId, NotificationUpdateRequest request) {
		User user = getUserByUserId(userId);
		user.updateNotification(request.dailyEnabled(), request.weeklyEnabled());

		UserAgreement agreement = userAgreementRepository.findByUser(user)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_AGREEMENT_NOT_FOUND));
		agreement.updateMarketingConsent(request.marketingConsent());
	}
}
