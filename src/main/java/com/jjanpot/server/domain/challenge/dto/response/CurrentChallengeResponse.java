package com.jjanpot.server.domain.challenge.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.challenge.entity.ChallengeWeek;
import com.jjanpot.server.domain.team.entity.Team;
import com.jjanpot.server.domain.team.entity.TeamRole;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 홈화면 현재 챌린지 조회 응답 DTO (GET /api/challenges/v1/current)
 *
 * status 값에 따라 홈화면이 3가지로 분기됨:
 *   - "NONE"    : 활성 챌린지 없음 → waiting, ongoing 모두 null
 *   - "WAITING" : 시작일 전 대기 중 → waiting 필드만 채워짐, ongoing은 null
 *   - "ONGOING" : 진행 중         → ongoing 필드만 채워짐, waiting은 null
 */
@Schema(description = "홈화면 현재 챌린지 조회 응답")
public record CurrentChallengeResponse(

	@Schema(description = "홈화면 상태 (NONE | WAITING | ONGOING)", example = "WAITING")
	String status,

	@Schema(description = "대기중인 챌린지 정보 (status가 WAITING일 때만 채워짐)")
	WaitingInfo waiting,

	@Schema(description = "진행중인 챌린지 정보 (status가 ONGOING일 때만 채워짐)")
	OngoingInfo ongoing
) {

	/**
	 * WAITING 상태 응답 정보
	 * 홈화면: 대기중인 챌린지 카드 표시
	 *   - 챌린지 제목, 기간, 목표 금액
	 *   - 팀장이면 "취소하기" 버튼 노출
	 *   - "초대코드 복사" 버튼
	 */
	@Schema(description = "대기중인 챌린지 정보")
	public record WaitingInfo(

		@Schema(description = "챌린지 ID", example = "1")
		Long challengeId,

		@Schema(description = "챌린지 제목", example = "배달을 아껴요")
		String title,

		@Schema(description = "챌린지 상태 한국어", example = "대기중인 챌린지")
		String challengeStatus,

		@Schema(description = "팀 전체 목표 절약 금액 - '30만원 목표로 1주동안 함께 절약하기'", example = "300000")
		int goalAmount,

		@Schema(description = "챌린지 시작 일시 - '26.07.15 - 26.07.21' 표시", example = "2026-07-15T00:00:00")
		@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
		LocalDateTime startDate,

		@Schema(description = "챌린지 종료 일시", example = "2026-07-21T00:00:00")
		@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
		LocalDateTime endDate,

		@Schema(description = "팀장 여부 - true면 '취소하기' 버튼 노출", example = "false")
		boolean isLeader,

		@Schema(description = "팀 초대코드 - '초대코드 복사' 버튼", example = "AB3K7Z")
		String inviteCode

	) {
		public static WaitingInfo from(Challenge challenge, TeamRole userRole, Team team) {
			return new WaitingInfo(
				challenge.getChallengeId(),
				challenge.getTitle(),
				challenge.getStatus().getDisplayName(),
				challenge.getGoalAmount(),
				challenge.getStartDate(),
				challenge.getEndDate(),
				userRole == TeamRole.LEADER,
				team.getInviteCode()
			);
		}
	}

	/**
	 * ONGOING 상태 응답 정보
	 * 홈화면: 챌린지 대시보드 현황 표시
	 *   - 상단 "{팀 이름} 팀은 ... 절약했어요!" 문구
	 *   - 팀 절약 금액, 개인 절약 금액
	 *   - 목표까지 X% 프로그레스 바
	 *   - D-day 배지 (endDate로 계산)
	 */
	@Schema(description = "진행중인 챌린지 정보")
	public record OngoingInfo(

		@Schema(description = "챌린지 ID", example = "1")
		Long challengeId,

		@Schema(description = "챌린지 제목", example = "배달을 아껴요")
		String title,

		@Schema(description = "챌린지 상태 한국어", example = "진행중인 챌린지")
		String challengeStatus,

		@Schema(description = "챌린지 종료 일시 - D-day 계산용", example = "2026-07-21T00:00:00")
		@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
		LocalDateTime endDate,

		// @Schema(description = "팀 이름 - '{팀 이름} 팀은 ... 절약했어요!' 문구", example = "배달을 아껴요")
		// String teamName,

		@Schema(description = "현재 주차 번호", example = "1")
		int weekNumber,

		@Schema(description = "이번 주 팀 목표 절약 금액", example = "300000")
		int weekGoalAmount,

		@Schema(description = "이번 주 팀 절약 금액", example = "250000")
		int teamWeekSavedAmount,

		@Schema(description = "팀 실시간 목표 달성률 (0~100, 100 초과 시 100으로 고정)", example = "80")
		int achievementRate,

		@Schema(description = "본인 개인 절약 금액", example = "25000")
		int personalWeekSavedAmount

	) {
		public static OngoingInfo from(Challenge challenge, Team team, ChallengeWeek currentWeek,
			int personalSavedAmount) {
			int savedAmount = currentWeek.getWeekSavedAmount();
			int goalAmount = currentWeek.getWeekGoalAmount();
			int rate = goalAmount > 0
				? Math.max(0, Math.min((int)((savedAmount * 100L) / goalAmount), 100))
				: 0;

			return new OngoingInfo(
				challenge.getChallengeId(),
				challenge.getTitle(),
				challenge.getStatus().getDisplayName(),
				challenge.getEndDate(),
				currentWeek.getWeekNumber(),
				goalAmount,
				savedAmount,
				rate,
				personalSavedAmount
			);
		}
	}

	// WAITING 상태
	public static CurrentChallengeResponse waiting(Challenge challenge, TeamRole userRole, Team team) {
		return new CurrentChallengeResponse(
			"WAITING",
			WaitingInfo.from(challenge, userRole, team),
			null
		);
	}

	// ONGOING 상태
	public static CurrentChallengeResponse ongoing(Challenge challenge, Team team, ChallengeWeek currentWeek,
		int personalSavedAmount) {
		return new CurrentChallengeResponse(
			"ONGOING",
			null,
			OngoingInfo.from(challenge, team, currentWeek, personalSavedAmount)
		);
	}

	// 활성 챌린지 없음
	public static CurrentChallengeResponse none() {
		return new CurrentChallengeResponse("NONE", null, null);
	}
}
