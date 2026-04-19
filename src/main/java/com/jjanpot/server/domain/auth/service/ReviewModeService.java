package com.jjanpot.server.domain.auth.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjanpot.server.domain.category.entity.Category;
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
	private static final String DEFAULT_PROFILE_IMAGE = "https://jjanpot-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/profile/defaultImg.png";
	private static final String[] REVIEW_NICKNAMES = {"너구리", "여우토끼", "윤뭉이"};
	private static final String[] REVIEW_MEMOS = {"카공", "결국 배달", "오늘도 성공"};

	private final ChallengeRepository challengeRepository;
	private final ChallengeWeekRepository challengeWeekRepository;
	private final ChallengeCategoryRepository challengeCategoryRepository;
	private final ChallengeTeamResultRepository challengeTeamResultRepository;
	private final TeamMembersRepository teamMembersRepository;
	private final UserRepository userRepository;
	private final CertificationRepository certificationRepository;
	private final ChallengeScheduler challengeScheduler;

	/** 챌린지 즉시 시작 (WAITING → ONGOING) + 리뷰용 참가자/인증 자동 생성 */
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

		// 리뷰용 참가자 3명 + 인증 포스트 자동 생성
		seedReviewData(challengeId);

		log.info("[ReviewMode] 챌린지 즉시 시작 + 리뷰 시드 데이터 생성: id={}", challengeId);
	}

	/** 챌린지 즉시 종료 (ONGOING → COMPLETED/FAILED, 결과 생성) */
	@Transactional
	public void finishChallenge(Long challengeId) {
		Challenge challenge = findChallenge(challengeId);

		if (challenge.getStatus() != ChallengeStatus.ONGOING) {
			throw new BusinessException(ErrorCode.CHALLENGE_NOT_ONGOING);
		}

		// 종료일을 현재 시점으로 맞춰 UI/결과 표시 일관성 유지
		LocalDateTime now = LocalDateTime.now(BUSINESS_ZONE);
		challenge.updateDates(challenge.getStartDate(), now);
		challengeWeekRepository.findByChallengeAndWeekNumber(challenge, 1)
			.ifPresent(week -> week.updateDates(week.getStartDate(), now));

		// 대상 챌린지만 결과 생성 (다른 ONGOING 챌린지에 영향 없음)
		challengeScheduler.finalizeChallenge(challenge);

		log.info("[ReviewMode] 챌린지 즉시 종료: id={}", challengeId);
	}

	/** 리뷰용 참가자 3명 + 인증 포스트 3개 생성 (신고/차단 시연용) */
	@Transactional
	public void seedReviewData(Long challengeId) {
		Challenge challenge = findChallenge(challengeId);
		if (challenge.getStatus() != ChallengeStatus.ONGOING || challenge.getStartDate() == null) {
			throw new BusinessException(ErrorCode.CHALLENGE_NOT_ONGOING);
		}
		Team team = challenge.getTeam();

		// 챌린지 카테고리 조회 (인증 생성 시 필요) — 챌린지에는 최소 1개 카테고리가 보장됨
		List<ChallengeCategory> categories = challengeCategoryRepository.findAllByChallenge(challenge);
		if (categories.isEmpty()) {
			throw new BusinessException(ErrorCode.CERTIFICATION_CATEGORY_NOT_IN_CHALLENGE);
		}
		Category category = categories.get(0).getCategory();

		ChallengeWeek week = challengeWeekRepository.findByChallengeAndWeekNumber(challenge, 1)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

		List<User> reviewUsers = new ArrayList<>();

		for (int i = 0; i < 3; i++) {
			final int index = i;
			// 리뷰용 유저 (이미 있으면 재사용)
			String providerId = "review_user_" + (index + 1);
			User reviewUser = userRepository.findByProviderAndProviderId(Provider.KAKAO, providerId)
				.orElseGet(() -> userRepository.save(
					User.create(Provider.KAKAO, providerId, REVIEW_NICKNAMES[index],
						"review" + (index + 1) + "@example.com", DEFAULT_PROFILE_IMAGE)));
			reviewUsers.add(reviewUser);

			// 팀 멤버로 추가 (이미 있으면 skip)
			if (!teamMembersRepository.existsByTeamAndUser(team, reviewUser)) {
				teamMembersRepository.save(TeamMembers.ofMember(team, reviewUser));
				team.increaseMemberCount();
			}

			// 인증 포스트 생성
			LocalDateTime spentAt = challenge.getStartDate().plusDays(index).plusHours(12);
			Certification reviewCert = Certification.create(challenge, reviewUser, category, week, SpendType.SPEND,
				10000, 5000, REVIEW_MEMOS[index], null, spentAt);
			certificationRepository.save(reviewCert);
		}

		log.info("[ReviewMode] 리뷰 시드 데이터 생성 완료: challengeId={}, userCount={}", challengeId, reviewUsers.size());
	}

	private Challenge findChallenge(Long challengeId) {
		return challengeRepository.findById(challengeId)
			.orElseThrow(() -> new BusinessException(ErrorCode.CHALLENGE_NOT_FOUND));
	}
}
