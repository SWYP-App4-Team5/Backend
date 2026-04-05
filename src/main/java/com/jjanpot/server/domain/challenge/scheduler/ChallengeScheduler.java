package com.jjanpot.server.domain.challenge.scheduler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.jjanpot.server.domain.certification.entity.Certification;
import com.jjanpot.server.domain.certification.repository.CertificationRepository;
import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.challenge.entity.ChallengeMemberResult;
import com.jjanpot.server.domain.challenge.entity.ChallengeStatus;
import com.jjanpot.server.domain.challenge.entity.ChallengeTeamResult;
import com.jjanpot.server.domain.challenge.entity.ChallengeWeek;
import com.jjanpot.server.domain.challenge.repository.ChallengeMemberResultRepository;
import com.jjanpot.server.domain.challenge.repository.ChallengeRepository;
import com.jjanpot.server.domain.challenge.repository.ChallengeTeamResultRepository;
import com.jjanpot.server.domain.challenge.repository.ChallengeWeekRepository;
import com.jjanpot.server.domain.team.entity.TeamMembers;
import com.jjanpot.server.domain.team.repository.TeamMembersRepository;
import com.jjanpot.server.domain.user.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChallengeScheduler {

	private final ChallengeRepository challengeRepository;
	private final ChallengeWeekRepository challengeWeekRepository;
	private final ChallengeMemberResultRepository challengeMemberResultRepository;
	private final ChallengeTeamResultRepository challengeTeamResultRepository;
	private final TeamMembersRepository teamMembersRepository;
	private final CertificationRepository certificationRepository;

	private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Seoul");

	/** 매일 자정: 시작일이 된 WAITING 챌린지를 ONGOING으로 전환 **/
	@Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
	@Transactional
	public void transitionWaitingToOngoing() {
		LocalDateTime now = LocalDateTime.now(BUSINESS_ZONE);

		List<Challenge> waitingChallenges = challengeRepository.findAllByStatus(ChallengeStatus.WAITING);

		List<Challenge> toStart = waitingChallenges.stream()
			.filter(c -> !c.getStartDate().isAfter(now))
			.toList();

		toStart.forEach(c -> c.updateStatus(ChallengeStatus.ONGOING));

		log.info("[ChallengeScheduler] WAITING → ONGOING 전환: {}건", toStart.size());
	}

	/** 매일 자정: 종료일이 지난 ONGOING 챌린지를 COMPLETED 또는 FAILED로 전환하고 결과를 저장 **/
	@Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
	@Transactional
	public void transitionOngoingToFinished() {
		LocalDateTime now = LocalDateTime.now(BUSINESS_ZONE);

		List<Challenge> toFinish = challengeRepository.findAllByStatusAndEndDateBefore(ChallengeStatus.ONGOING, now);

		for (Challenge challenge : toFinish) {
			// 이미 결과가 생성된 챌린지는 skip (중복 실행 방지)
			if (challengeTeamResultRepository.findByChallenge(challenge).isPresent()) {
				log.info("[ChallengeScheduler] 이미 결과 존재, skip: id={}", challenge.getChallengeId());
				continue;
			}

			List<TeamMembers> members = teamMembersRepository.findAllByTeam(challenge.getTeam());

			List<ChallengeMemberResult> memberResults = members.stream()
				.map(m -> buildMemberResult(challenge, m.getUser()))
				.toList();

			challengeMemberResultRepository.saveAll(memberResults);

			ChallengeTeamResult teamResult = buildTeamResult(challenge, memberResults);
			challengeTeamResultRepository.save(teamResult);

			ChallengeStatus newStatus = teamResult.isTeamSuccess()
				? ChallengeStatus.COMPLETED
				: ChallengeStatus.FAILED;
			challenge.updateStatus(newStatus);

			log.info("[ChallengeScheduler] 챌린지 종료: id={}, status={}", challenge.getChallengeId(), newStatus);
		}

		log.info("[ChallengeScheduler] ONGOING → 종료 처리: {}건", toFinish.size());
	}

	private ChallengeMemberResult buildMemberResult(Challenge challenge, User user) {
		List<Certification> certs = certificationRepository.findAllByChallengeAndUser(challenge, user);

		long totalSavedAmount = certs.stream().mapToLong(Certification::getSavedAmount).sum();
		int totalCertCount = certs.size();
		boolean isPersonalSuccess = totalSavedAmount >= challenge.getMinPersonalGoalAmount();

		// 주간 최소 2회 미달 시 규칙 위반
		boolean isRuleViolated = totalCertCount < 2;

		// 인증이 있는 날의 수 / 7일 * 100 (MVP: 1주 고정)
		long distinctDays = certs.stream()
			.map(c -> c.getCreatedAt().toLocalDate())
			.distinct()
			.count();
		BigDecimal weeklyParticipationRate = BigDecimal.valueOf(distinctDays)
			.divide(BigDecimal.valueOf(7), 2, RoundingMode.HALF_UP)
			.multiply(BigDecimal.valueOf(100));

		return ChallengeMemberResult.builder()
			.challenge(challenge)
			.user(user)
			.totalSavedAmount(totalSavedAmount)
			.totalCertCount(totalCertCount)
			.isPersonalSuccess(isPersonalSuccess)
			.isRuleViolated(isRuleViolated)
			.weeklyParticipationRate(weeklyParticipationRate)
			.build();
	}

	private ChallengeTeamResult buildTeamResult(Challenge challenge, List<ChallengeMemberResult> memberResults) {
		// ChallengeWeek.weekSavedAmount는 팀 전체 누적 절약 금액 (인증 시 실시간 반영됨)
		List<ChallengeWeek> weeks = challengeWeekRepository.findAllByChallenge(challenge);
		int teamTotalSaved = weeks.stream().mapToInt(ChallengeWeek::getWeekSavedAmount).sum();
		int totalCertCount = memberResults.stream().mapToInt(ChallengeMemberResult::getTotalCertCount).sum();

		// 팀 성공 조건: 전원 개인 최소 금액 충족 AND 팀 공동 목표 금액 충족
		boolean allPersonalSuccess = memberResults.stream().allMatch(ChallengeMemberResult::getIsPersonalSuccess);
		boolean isTeamSuccess = allPersonalSuccess && teamTotalSaved >= challenge.getGoalAmount();

		BigDecimal achievementRate = challenge.getGoalAmount() > 0
			? BigDecimal.valueOf(teamTotalSaved)
				.divide(BigDecimal.valueOf(challenge.getGoalAmount()), 2, RoundingMode.HALF_UP)
				.multiply(BigDecimal.valueOf(100))
			: BigDecimal.ZERO;

		int memberCount = memberResults.size();

		BigDecimal avgWeeklyCertCount = memberCount > 0
			? BigDecimal.valueOf(totalCertCount)
				.divide(BigDecimal.valueOf(memberCount), 2, RoundingMode.HALF_UP)
			: BigDecimal.ZERO;

		BigDecimal avgWeeklyParticipationRate = memberCount > 0
			? memberResults.stream()
				.map(ChallengeMemberResult::getWeeklyParticipationRate)
				.reduce(BigDecimal.ZERO, BigDecimal::add)
				.divide(BigDecimal.valueOf(memberCount), 2, RoundingMode.HALF_UP)
			: BigDecimal.ZERO;

		return ChallengeTeamResult.builder()
			.challenge(challenge)
			.goalAmount(challenge.getGoalAmount())
			.totalSavedAmount(teamTotalSaved)
			.totalCertCount(totalCertCount)
			.isTeamSuccess(isTeamSuccess)
			.achievementRate(achievementRate)
			.avgWeeklyCertCount(avgWeeklyCertCount)
			.avgWeeklyParticipationRate(avgWeeklyParticipationRate)
			.build();
	}
}
