package com.jjanpot.server.domain.challenge.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjanpot.server.domain.category.entity.Category;
import com.jjanpot.server.domain.category.repository.CategoryRepository;
import com.jjanpot.server.domain.challenge.dto.ChallengeCategoryRequest;
import com.jjanpot.server.domain.challenge.dto.CreateChallengeRequest;
import com.jjanpot.server.domain.challenge.dto.CreateChallengeResponse;
import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.challenge.entity.ChallengeCategory;
import com.jjanpot.server.domain.challenge.entity.ChallengeMinGoalPolicy;
import com.jjanpot.server.domain.challenge.entity.ChallengeWeek;
import com.jjanpot.server.domain.challenge.repository.ChallengeCategoryRepository;
import com.jjanpot.server.domain.challenge.repository.ChallengeMinGoalPolicyRepository;
import com.jjanpot.server.domain.challenge.repository.ChallengeRepository;
import com.jjanpot.server.domain.challenge.repository.ChallengeWeekRepository;
import com.jjanpot.server.domain.team.entity.Team;
import com.jjanpot.server.domain.team.entity.TeamMembers;
import com.jjanpot.server.domain.team.repository.TeamMembersRepository;
import com.jjanpot.server.domain.team.repository.TeamRepository;
import com.jjanpot.server.domain.user.entity.User;
import com.jjanpot.server.global.exception.BusinessException;
import com.jjanpot.server.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeService {

	private static final int CHALLENGE_DURATION_WEEKS = 1;
	private static final int INVITE_CODE_LENGTH = 6;
	private static final String INVITE_CODE_CHARS = "ABCDEFGHJKMNPQRSTUVWXYZ23456789"; // O, I, L, 0, 1 제외 (혼동 가능성 제외)
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	private final TeamRepository teamRepository;
	private final TeamMembersRepository teamMembersRepository;
	private final ChallengeRepository challengeRepository;
	private final ChallengeWeekRepository challengeWeekRepository;
	private final ChallengeCategoryRepository challengeCategoryRepository;
	private final ChallengeMinGoalPolicyRepository challengeMinGoalPolicyRepository;
	private final CategoryRepository categoryRepository;

	/** 챌린지 생성 **/
	@Transactional
	public CreateChallengeResponse createChallenge(User user, CreateChallengeRequest request) {

		// 1. ChallengeMinGoalPolicy 조회
		validateGoalAmount(request.memberCount(), request.goalAmount());

		// 2. 초대코드 생성
		String inviteCode = generateUniqueInviteCode();

		// 3. Team 저장
		Team team = teamRepository.save(
			Team.of(request.teamName(), inviteCode, request.teamType(), request.memberCount()));

		// 4. TeamMembers 저장
		teamMembersRepository.save(TeamMembers.ofLeader(team, user));

		LocalDateTime startDateTime = request.startDate().atStartOfDay();
		LocalDateTime endDateTime = startDateTime.plusWeeks(CHALLENGE_DURATION_WEEKS);

		// 5. Challenge 저장
		Challenge challenge = challengeRepository.save(Challenge.from(request, team, startDateTime, endDateTime));

		// 6. ChallengeWeek 저장
		challengeWeekRepository.save(ChallengeWeek.firstWeek(challenge, startDateTime, endDateTime));

		// 7. Category 조회 + ChallengeCategory 저장
		List<ChallengeCategory> challengeCategories = saveChallengeCategories(challenge, request.categories());

		return CreateChallengeResponse.from(challenge, team, challengeCategories);
	}

	// 인원 수에 따른 팀 전체 목표 금액 최소 기준 검증
	private void validateGoalAmount(int memberCount, long goalAmount) {
		ChallengeMinGoalPolicy policy = challengeMinGoalPolicyRepository
			.findByMemberCount(memberCount)
			.orElseThrow(() -> new BusinessException(ErrorCode.CHALLENGE_MIN_GOAL_POLICY_NOT_FOUND));

		if (goalAmount < policy.getMinAmount()) {
			throw new BusinessException(
				ErrorCode.GOAL_AMOUNT_BELOW_MINIMUM,
				String.format("%d명 팀의 최소 목표 금액은 %,d원 이상이어야 합니다.", memberCount, policy.getMinAmount())
			);
		}
	}

	// 팀 초대 코드 생성 (6자리 랜덤 코드, 중복 시 재생성)
	private String generateUniqueInviteCode() {
		String code;
		do {
			code = IntStream.range(0, INVITE_CODE_LENGTH)
				.mapToObj(
					i -> String.valueOf(INVITE_CODE_CHARS.charAt(SECURE_RANDOM.nextInt(INVITE_CODE_CHARS.length()))))
				.collect(Collectors.joining());
		} while (teamRepository.existsByInviteCode(code));
		return code;
	}

	// 카테고리 ID 유효성 검증 후 챌린지 카테고리 저장
	private List<ChallengeCategory> saveChallengeCategories(
		Challenge challenge,
		List<ChallengeCategoryRequest> categoryRequests
	) {
		List<Long> categoryIds = categoryRequests.stream()
			.map(ChallengeCategoryRequest::categoryId)
			.toList();

		Map<Long, Category> categoryMap = categoryRepository.findAllById(categoryIds)
			.stream()
			.collect(Collectors.toMap(Category::getCategoryId, c -> c));

		if (categoryMap.size() != categoryIds.size()) {
			throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
		}

		List<ChallengeCategory> challengeCategories = categoryRequests.stream()
			.map(req -> ChallengeCategory.of(
				challenge,
				categoryMap.get(req.categoryId()),
				req.amount()
			))
			.toList();

		return challengeCategoryRepository.saveAll(challengeCategories);
	}
}
