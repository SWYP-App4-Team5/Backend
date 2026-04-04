package com.jjanpot.server.domain.challenge.service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjanpot.server.domain.category.entity.Category;
import com.jjanpot.server.domain.category.repository.CategoryRepository;
import com.jjanpot.server.domain.certification.repository.CertificationRepository;
import com.jjanpot.server.domain.challenge.dto.request.ChallengeCategoryRequest;
import com.jjanpot.server.domain.challenge.dto.request.CreateChallengeRequest;
import com.jjanpot.server.domain.challenge.dto.response.ChallengeDetailResponse;
import com.jjanpot.server.domain.challenge.dto.response.ChallengeMembersResponse;
import com.jjanpot.server.domain.challenge.dto.response.ChallengeResultResponse;
import com.jjanpot.server.domain.challenge.dto.response.ChallengeStatsResponse;
import com.jjanpot.server.domain.challenge.dto.response.CreateChallengeResponse;
import com.jjanpot.server.domain.challenge.dto.response.CurrentChallengeResponse;
import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.challenge.entity.ChallengeCategory;
import com.jjanpot.server.domain.challenge.entity.ChallengeMemberResult;
import com.jjanpot.server.domain.challenge.entity.ChallengeMinGoalPolicy;
import com.jjanpot.server.domain.challenge.entity.ChallengeStatus;
import com.jjanpot.server.domain.challenge.entity.ChallengeTeamResult;
import com.jjanpot.server.domain.challenge.entity.ChallengeWeek;
import com.jjanpot.server.domain.challenge.repository.ChallengeCategoryRepository;
import com.jjanpot.server.domain.challenge.repository.ChallengeMemberResultRepository;
import com.jjanpot.server.domain.challenge.repository.ChallengeMinGoalPolicyRepository;
import com.jjanpot.server.domain.challenge.repository.ChallengeRepository;
import com.jjanpot.server.domain.challenge.repository.ChallengeTeamResultRepository;
import com.jjanpot.server.domain.challenge.repository.ChallengeWeekRepository;
import com.jjanpot.server.domain.team.entity.Team;
import com.jjanpot.server.domain.team.entity.TeamMembers;
import com.jjanpot.server.domain.team.entity.TeamRole;
import com.jjanpot.server.domain.team.repository.TeamMembersRepository;
import com.jjanpot.server.domain.team.repository.TeamRepository;
import com.jjanpot.server.domain.user.entity.User;
import com.jjanpot.server.domain.user.repository.UserRepository;
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
	private static final ZoneId BUSINESS_ZONE_ID = ZoneId.of("Asia/Seoul");
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	private final UserRepository userRepository;
	private final TeamRepository teamRepository;
	private final TeamMembersRepository teamMembersRepository;
	private final ChallengeRepository challengeRepository;
	private final ChallengeWeekRepository challengeWeekRepository;
	private final ChallengeCategoryRepository challengeCategoryRepository;
	private final ChallengeMinGoalPolicyRepository challengeMinGoalPolicyRepository;
	private final CategoryRepository categoryRepository;
	private final CertificationRepository certificationRepository;
	private final ChallengeTeamResultRepository challengeTeamResultRepository;
	private final ChallengeMemberResultRepository challengeMemberResultRepository;

	/** 챌린지 생성 **/
	@Transactional
	public CreateChallengeResponse createChallenge(Long userId, CreateChallengeRequest request) {
		User user = findUserForUpdate(userId);
		validateNoActiveChallenge(userId);
		validateStartDate(request.startDate());

		// 1. ChallengeMinGoalPolicy 조회
		validateGoalAmount(request.maxMemberCount(), request.goalAmount());

		// 2. 초대코드 생성
		String inviteCode = generateUniqueInviteCode();

		// 3. Team 저장
		Team team = teamRepository.save(
			Team.of(inviteCode, request.teamType(), request.maxMemberCount()));

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

	/** 챌린지 취소 (팀장 전용, WAITING 상태에서만 가능) **/
	@Transactional
	public void cancelChallenge(Long userId, Long challengeId) {
		User user = findUser(userId);
		Challenge challenge = challengeRepository.findById(challengeId)
			.orElseThrow(() -> new BusinessException(ErrorCode.CHALLENGE_NOT_FOUND));

		TeamMembers membership = teamMembersRepository.findByTeamAndUser(challenge.getTeam(), user)
			.orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN));

		if (membership.getRole() != TeamRole.LEADER) {
			throw new BusinessException(ErrorCode.CHALLENGE_LEADER_REQUIRED);
		}

		if (challenge.getStatus() != ChallengeStatus.WAITING) {
			throw new BusinessException(ErrorCode.CHALLENGE_CANCEL_FORBIDDEN);
		}

		challenge.updateStatus(ChallengeStatus.CANCELLED);
	}

	/** 챌린지 결과 조회 (COMPLETED/FAILED) **/
	public ChallengeResultResponse getChallengeResult(Long userId, Long challengeId) {
		User user = findUser(userId);
		Challenge challenge = challengeRepository.findById(challengeId)
			.orElseThrow(() -> new BusinessException(ErrorCode.CHALLENGE_NOT_FOUND));

		teamMembersRepository.findByTeamAndUser(challenge.getTeam(), user)
			.orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN));

		if (challenge.getStatus() != ChallengeStatus.COMPLETED
			&& challenge.getStatus() != ChallengeStatus.FAILED) {
			throw new BusinessException(ErrorCode.CHALLENGE_RESULT_NOT_READY);
		}

		ChallengeTeamResult teamResult = challengeTeamResultRepository.findByChallenge(challenge)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

		ChallengeMemberResult memberResult = challengeMemberResultRepository.findByChallengeAndUser(challenge, user)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

		List<ChallengeCategory> categories = challengeCategoryRepository.findAllByChallenge(challenge);

		return ChallengeResultResponse.from(teamResult, memberResult, categories);
	}

	/** 챌린지 상세 조회 **/
	public ChallengeDetailResponse getChallengeDetail(Long userId, Long challengeId) {
		User user = findUser(userId);
		Challenge challenge = challengeRepository.findById(challengeId)
			.orElseThrow(() -> new BusinessException(ErrorCode.CHALLENGE_NOT_FOUND));

		TeamMembers membership = teamMembersRepository.findByTeamAndUser(challenge.getTeam(), user)
			.orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN));

		List<ChallengeCategory> categories = challengeCategoryRepository.findAllByChallenge(challenge);

		return ChallengeDetailResponse.from(challenge, categories, membership.getRole());
	}

	/** 팀/개인 절약 현황 통계 조회 (홈화면 스크롤 시) **/
	public ChallengeStatsResponse getChallengeStats(Long userId, Long challengeId) {
		User user = findUser(userId);
		Challenge challenge = challengeRepository.findById(challengeId)
			.orElseThrow(() -> new BusinessException(ErrorCode.CHALLENGE_NOT_FOUND));

		teamMembersRepository.findByTeamAndUser(challenge.getTeam(), user)
			.orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN));

		List<TeamMembers> members = teamMembersRepository.findAllByTeam(challenge.getTeam());
		int memberCount = members.size();

		// 챌린지 시작일부터 오늘(또는 종료일)까지 경과일 수
		LocalDate startDate = challenge.getStartDate().toLocalDate();
		LocalDate today = LocalDate.now(BUSINESS_ZONE_ID);
		LocalDate endDate = challenge.getEndDate().toLocalDate();
		LocalDate effectiveEnd = today.isBefore(endDate) ? today : endDate;
		int elapsedDays = (int) (effectiveEnd.toEpochDay() - startDate.toEpochDay()) + 1;
		if (elapsedDays < 1) {
			elapsedDays = 1;
		}

		// ── 팀 절약 현황 ──
		// 인증평균: 팀원 1인당 주간 인증 횟수 평균 (소수점 버림 → 소수 첫째 자리까지)
		Map<Long, Long> certCountMap = certificationRepository
			.countCertPerUserByChallenge(challenge)
			.stream()
			.collect(Collectors.toMap(
				row -> (Long) row[0],
				row -> (Long) row[1]
			));
		long totalCertCount = certCountMap.values().stream().mapToLong(Long::longValue).sum();
		int teamAvgCert = memberCount > 0
			? (int) (totalCertCount / memberCount)
			: 0;

		// 참여율: 인증 1회 이상 한 팀원 수 / 전체 팀원 수 (소수점 버림)
		long activeMemberCount = certCountMap.size();
		int teamParticipationRate = memberCount > 0
			? (int) (activeMemberCount * 100 / memberCount)
			: 0;

		// 연속활동: 모든 팀원이 인증한 연속 일수 (최근 기준 역산)
		int teamConsecutiveDays = calculateTeamStreak(challenge, memberCount, effectiveEnd);

		// ── 개인 절약 현황 ──
		// 인증횟수: 본인 주간 총 인증 횟수
		int personalCertCount = certCountMap.getOrDefault(userId, 0L).intValue();

		// 참여율: 본인이 인증한 날 / 경과일 (소수점 버림)
		List<LocalDate> personalCertDates = certificationRepository
			.findDistinctCertDatesByUser(challenge, user)
			.stream()
			.map(dateValue -> {
				if (dateValue instanceof LocalDate ld) {
					return ld;
				}
				return ((java.sql.Date) dateValue).toLocalDate();
			})
			.toList();
		int personalParticipationRate = (int) (personalCertDates.size() * 100 / elapsedDays);

		// 연속활동: 본인 연속 인증일 (최근 기준 역산)
		int personalConsecutiveDays = calculatePersonalStreak(personalCertDates, effectiveEnd);

		return new ChallengeStatsResponse(
			new ChallengeStatsResponse.TeamStats(teamAvgCert, teamParticipationRate, teamConsecutiveDays),
			new ChallengeStatsResponse.PersonalStats(personalCertCount, personalParticipationRate, personalConsecutiveDays)
		);
	}

	/** 팀 연속활동일 계산: effectiveEnd(오늘 또는 종료일)부터 역산하여 모든 팀원이 인증한 연속 일수 */
	private int calculateTeamStreak(Challenge challenge, int memberCount, LocalDate effectiveEnd) {
		List<Object[]> dailyUserCounts = certificationRepository
			.countDistinctUsersByDateForChallenge(challenge);

		// 모든 팀원이 인증한 날짜만 필터링
		List<LocalDate> fullParticipationDates = dailyUserCounts.stream()
			.filter(row -> ((Number) row[1]).intValue() >= memberCount)
			.map(row -> {
				Object dateValue = row[0];
				if (dateValue instanceof LocalDate ld) {
					return ld;
				}
				return ((java.sql.Date) dateValue).toLocalDate();
			})
			.toList();

		return calculateStreakFromDate(fullParticipationDates, effectiveEnd);
	}

	/** 개인 연속활동일 계산: effectiveEnd(오늘 또는 종료일)부터 역산하여 연속 인증일 */
	private int calculatePersonalStreak(List<LocalDate> certDates, LocalDate effectiveEnd) {
		return calculateStreakFromDate(certDates, effectiveEnd);
	}

	/** 기준일(effectiveEnd)부터 역산하여 연속 일수 계산. 기준일에 인증이 없으면 0 반환 */
	private int calculateStreakFromDate(List<LocalDate> sortedDates, LocalDate baseDate) {
		if (sortedDates.isEmpty()) {
			return 0;
		}

		// 기준일(오늘 또는 종료일)에 인증이 없으면 연속 끊김 → 0
		if (!sortedDates.contains(baseDate)) {
			return 0;
		}

		int streak = 1;
		LocalDate current = baseDate.minusDays(1);
		for (int i = sortedDates.size() - 2; i >= 0; i--) {
			if (sortedDates.get(i).equals(current)) {
				streak++;
				current = current.minusDays(1);
			} else if (sortedDates.get(i).isBefore(current)) {
				break;
			}
		}
		return streak;
	}

	/** 챌린지 팀원 절약 현황 조회 **/
	public ChallengeMembersResponse getChallengeMembers(Long userId, Long challengeId) {
		User user = findUser(userId);
		Challenge challenge = challengeRepository.findById(challengeId)
			.orElseThrow(() -> new BusinessException(ErrorCode.CHALLENGE_NOT_FOUND));

		teamMembersRepository.findByTeamAndUser(challenge.getTeam(), user)
			.orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN));

		// 팀원 목록 조회
		List<TeamMembers> members = teamMembersRepository.findAllByTeam(challenge.getTeam());

		// 팀원별 절약 금액 SUM 조회
		Map<Long, Integer> savedAmountMap = certificationRepository
			.sumSavedAmountPerUserByChallenge(challenge)
			.stream()
			.collect(Collectors.toMap(
				row -> (Long) row[0],
				row -> ((Number) row[1]).intValue()
			));

		// 팀 전체 절약 금액
		int totalSavedAmount = savedAmountMap.values().stream()
			.mapToInt(Integer::intValue)
			.sum();

		// 팀원 정보 매핑
		List<ChallengeMembersResponse.MemberSavingInfo> memberInfos = members.stream()
			.map(member -> {
				User memberUser = member.getUser();
				return new ChallengeMembersResponse.MemberSavingInfo(
					memberUser.getUserId(),
					memberUser.getNickname(),
					memberUser.getProfileImageUrl(),
					savedAmountMap.getOrDefault(memberUser.getUserId(), 0),
					memberUser.getUserId().equals(userId)
				);
			})
			.toList();

		return new ChallengeMembersResponse(
			challenge.getChallengeId(),
			challenge.getTitle(),
			challenge.getStartDate().toString(),
			totalSavedAmount,
			challenge.getGoalAmount(),
			memberInfos
		);
	}

	/** 현재 유저의 활성된 챌린지 조회 (홈 화면) **/
	//  로그인한 유저의 현재 활성 챌린지를 찾아 홈 화면 상태를 결정
	public CurrentChallengeResponse getCurrentChallenge(Long userId) {
		User user = findUser(userId);

		// 1. 이 유저가 속한 모든 팀 멤버십 조회 (유저가 과거에 참여했던 팀까지 전부 가져옴)
		List<TeamMembers> memberships = teamMembersRepository.findAllByUser(user);

		// 2. 각 팀에서 활성 챌린지(WAITING or ONGOING) 탐색
		for (TeamMembers membership : memberships) {

			// 팀마다 순회하면서 진행 중인 챌린지가 있는지 확인
			Optional<Challenge> activeChallenge = challengeRepository.findFirstByTeamAndStatusIn(
				membership.getTeam(),
				List.of(ChallengeStatus.WAITING, ChallengeStatus.ONGOING)
			);

			// 진행 중인 챌린지 없으면 다음 팀 확인
			if (activeChallenge.isEmpty()) {
				continue;
			}

			Challenge challenge = activeChallenge.get();

			// 3-A. WAITING이면 대기중인 챌린지 정보 반환
			if (challenge.getStatus() == ChallengeStatus.WAITING) {
				return CurrentChallengeResponse.waiting(challenge, membership.getRole(), membership.getTeam());
			}

			// 3-B. ONGOING이면 현재 주차 정보 + 개인 절약 금액 포함해서 반환
			ChallengeWeek currentWeek = challengeWeekRepository
				.findByChallengeAndWeekNumber(challenge, 1)
				.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

			// 개인 절약 금액 조회 (SUM)
			int personalSavedAmount = certificationRepository
				.sumSavedAmountPerUserByChallenge(challenge)
				.stream()
				.filter(row -> userId.equals(row[0]))
				.map(row -> ((Number) row[1]).intValue())
				.findFirst()
				.orElse(0);

			return CurrentChallengeResponse.ongoing(challenge, membership.getTeam(), currentWeek, personalSavedAmount);
		}

		// 4. 모든 팀을 확인했는데 활성 챌린지가 없으면 홈 화면에서 "대기 중인 챌린지 없음" 표시
		return CurrentChallengeResponse.none();
	}

	private User findUser(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
	}

	private User findUserForUpdate(Long userId) {
		return userRepository.findByIdForUpdate(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
	}

	// 활성 챌린지 중복 참여 방지 검증
	private void validateNoActiveChallenge(Long userId) {
		if (challengeRepository.existsActiveByUserIdAndStatusIn(
			userId, List.of(ChallengeStatus.WAITING, ChallengeStatus.ONGOING))) {
			throw new BusinessException(ErrorCode.CHALLENGE_ALREADY_ACTIVE);
		}
	}

	// 인원 수에 따른 팀 전체 목표 금액 최소 기준 검증
	private void validateGoalAmount(int memberCount, int goalAmount) {
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

	private void validateStartDate(LocalDate startDate) {
		LocalDate today = LocalDate.now(BUSINESS_ZONE_ID);
		if (startDate.isBefore(today)) {
			throw new BusinessException(
				ErrorCode.INVALID_CHALLENGE_START_DATE,
				String.format("챌린지 시작일은 %s 기준 오늘(%s)보다 이전일 수 없습니다.", BUSINESS_ZONE_ID, today)
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
