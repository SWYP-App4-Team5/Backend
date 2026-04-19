package com.jjanpot.server.domain.auth.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjanpot.server.domain.category.entity.Category;
import com.jjanpot.server.domain.category.repository.CategoryRepository;
import com.jjanpot.server.domain.certification.entity.Certification;
import com.jjanpot.server.domain.certification.entity.SpendType;
import com.jjanpot.server.domain.certification.repository.CertificationRepository;
import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.challenge.entity.ChallengeCategory;
import com.jjanpot.server.domain.challenge.entity.ChallengeStatus;
import com.jjanpot.server.domain.challenge.entity.ChallengeWeek;
import com.jjanpot.server.domain.challenge.repository.ChallengeCategoryRepository;
import com.jjanpot.server.domain.challenge.repository.ChallengeRepository;
import com.jjanpot.server.domain.challenge.repository.ChallengeTeamResultRepository;
import com.jjanpot.server.domain.challenge.repository.ChallengeWeekRepository;
import com.jjanpot.server.domain.challenge.scheduler.ChallengeScheduler;
import com.jjanpot.server.domain.team.entity.Team;
import com.jjanpot.server.domain.team.entity.TeamMembers;
import com.jjanpot.server.domain.team.repository.TeamMembersRepository;
import com.jjanpot.server.domain.user.entity.Provider;
import com.jjanpot.server.domain.user.entity.User;
import com.jjanpot.server.domain.user.repository.UserRepository;
import com.jjanpot.server.global.exception.BusinessException;
import com.jjanpot.server.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewModeService {

	private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Seoul");
	private static final String FAKE_PROFILE_IMAGE = "https://jjanpot-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/profile/defaultImg.png";

	private final ChallengeRepository challengeRepository;
	private final ChallengeWeekRepository challengeWeekRepository;
	private final ChallengeCategoryRepository challengeCategoryRepository;
	private final ChallengeTeamResultRepository challengeTeamResultRepository;
	private final TeamMembersRepository teamMembersRepository;
	private final UserRepository userRepository;
	private final CertificationRepository certificationRepository;
	private final CategoryRepository categoryRepository;
	private final ChallengeScheduler challengeScheduler;

	/** 챌린지 즉시 시작 (WAITING → ONGOING) + 가짜 참가자/포스트 자동 생성 */
	@Transactional
	public void startChallenge(Long challengeId) {
		Challenge challenge = findChallenge(challengeId);

		if (challenge.getStatus() != ChallengeStatus.WAITING) {
			throw new BusinessException(ErrorCode.CHALLENGE_NOT_JOINABLE);
		}

		challenge.updateStatus(ChallengeStatus.ONGOING);

		// 시작/종료일을 현재 기준으로 재설정 (7일)
		LocalDateTime now = LocalDateTime.now(BUSINESS_ZONE);
		challenge.updateDates(now, now.plusWeeks(1));

		// ChallengeWeek도 업데이트
		challengeWeekRepository.findByChallengeAndWeekNumber(challenge, 1)
			.ifPresent(week -> week.updateDates(now, now.plusWeeks(1)));

		// 가짜 참가자 3명 + 신고용 포스트 자동 생성
		seedFakeData(challengeId);

		log.info("[ReviewMode] 챌린지 즉시 시작 + 가짜 데이터 생성: id={}", challengeId);
	}

	/** 챌린지 즉시 종료 (ONGOING → COMPLETED/FAILED, 결과 생성) */
	@Transactional
	public void finishChallenge(Long challengeId) {
		Challenge challenge = findChallenge(challengeId);

		if (challenge.getStatus() != ChallengeStatus.ONGOING) {
			throw new BusinessException(ErrorCode.CHALLENGE_NOT_ONGOING);
		}

		// 종료일을 과거로 설정하여 스케줄러가 처리하도록
		challenge.updateDates(challenge.getStartDate(), LocalDateTime.now(BUSINESS_ZONE).minusMinutes(1));

		challengeWeekRepository.findByChallengeAndWeekNumber(challenge, 1)
			.ifPresent(week -> week.updateDates(week.getStartDate(), LocalDateTime.now(BUSINESS_ZONE).minusMinutes(1)));

		// 스케줄러 실행하여 결과 생성
		challengeScheduler.transitionOngoingToFinished();

		log.info("[ReviewMode] 챌린지 즉시 종료: id={}", challengeId);
	}

	/** 가짜 참가자 3명 + 가짜 인증 포스트 3개 생성 (신고/차단 테스트용) */
	@Transactional
	public void seedFakeData(Long challengeId) {
		Challenge challenge = findChallenge(challengeId);
		Team team = challenge.getTeam();

		// 챌린지 카테고리 조회 (인증 생성 시 필요)
		List<ChallengeCategory> categories = challengeCategoryRepository.findAllByChallenge(challenge);
		Category category = categories.isEmpty()
			? categoryRepository.findAll().get(0)
			: categories.get(0).getCategory();

		ChallengeWeek week = challengeWeekRepository.findByChallengeAndWeekNumber(challenge, 1)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

		String[] fakeNames = {"신고테스트유저A", "신고테스트유저B", "신고테스트유저C"};
		String[] fakeMemos = {
			"부적절한 내용 테스트 포스트입니다",
			"신고용 가짜 인증 포스트입니다",
			"차단 테스트를 위한 포스트입니다"
		};

		List<User> fakeUsers = new ArrayList<>();

		for (int i = 0; i < 3; i++) {
			final int index = i;
			// 가짜 유저 생성 (이미 있으면 재사용)
			String providerId = "review_fake_user_" + (index + 1);
			User fakeUser = userRepository.findByProviderAndProviderId(Provider.KAKAO, providerId)
				.orElseGet(() -> userRepository.save(
					User.create(Provider.KAKAO, providerId, fakeNames[index], "fake" + (index + 1) + "@review.test", FAKE_PROFILE_IMAGE)
				));
			fakeUsers.add(fakeUser);

			// 팀 멤버로 추가 (이미 있으면 skip)
			if (!teamMembersRepository.existsByTeamAndUser(team, fakeUser)) {
				teamMembersRepository.save(TeamMembers.ofMember(team, fakeUser));
				team.increaseMemberCount();
			}

			// 가짜 인증 포스트 생성
			LocalDateTime spentAt = challenge.getStartDate().plusDays(index).plusHours(12);
			Certification fakeCert = Certification.create(
				challenge, fakeUser, category, week,
				SpendType.SPEND, 10000, 5000, fakeMemos[index],
				null, spentAt
			);
			certificationRepository.save(fakeCert);
		}

		log.info("[ReviewMode] 가짜 데이터 생성 완료: challengeId={}, fakeUsers={}", challengeId, fakeUsers.size());
	}

	private Challenge findChallenge(Long challengeId) {
		return challengeRepository.findById(challengeId)
			.orElseThrow(() -> new BusinessException(ErrorCode.CHALLENGE_NOT_FOUND));
	}
}
