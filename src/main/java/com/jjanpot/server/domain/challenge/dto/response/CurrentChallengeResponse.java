package com.jjanpot.server.domain.challenge.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.challenge.entity.ChallengeCategory;
import com.jjanpot.server.domain.challenge.entity.ChallengeWeek;
import com.jjanpot.server.domain.team.entity.Team;
import com.jjanpot.server.domain.team.entity.TeamRole;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 홈화면 현재 챌린지 조회 응답 DTO (GET /api/challenges/v1/current)
 *
 * status 값에 따라 홈화면이 3가지로 분기됨:
 *   - "NONE"    : 활성 챌린지 없음 → challenge 필드는 null
 *                 → 홈화면에서 "챌린지 만들기 / 초대코드 입력" 화면 표시
 *   - "WAITING" : 시작일 전 대기 중 → challenge 필드에 챌린지 정보 포함
 *                 → 홈화면에서 대기 중 챌린지 화면 표시
 *   - "ONGOING" : 진행 중 → challenge 필드에 주차 정보(weekNumber 등) 추가 포함
 *                 → 홈화면에서 진행 중 대시보드 화면 표시
 */
@Schema(description = "홈화면 현재 챌린지 조회 응답")
public record CurrentChallengeResponse(

	@Schema(description = "홈화면 상태 (NONE | WAITING | ONGOING)", example = "ONGOING")
	String status,

	@Schema(description = "활성 챌린지 정보 (status가 NONE이면 null)")
	ChallengeInfo challenge
) {

	/**
	 * 챌린지 상세 정보
	 * WAITING과 ONGOING 모두 공통 필드를 가지며,
	 * ONGOING일 때만 weekNumber, weekGoalAmount, weekSavedAmount가 채워짐
	 */
	@Schema(description = "챌린지 정보")
	public record ChallengeInfo(

		@Schema(description = "챌린지 ID", example = "1")
		Long challengeId,

		@Schema(description = "챌린지 제목", example = "배달을 아껴요")
		String title,

		@Schema(description = "챌린지 상태 한국어", example = "진행중인 챌린지")
		String challengeStatus,

		@Schema(description = "팀 전체 목표 절약 금액", example = "300000")
		int goalAmount,

		@Schema(description = "인당 최소 목표 절약 금액", example = "20000")
		int minPersonalGoalAmount,

		@Schema(description = "챌린지 시작 일시", example = "2026-07-15T00:00:00")
		LocalDateTime startDate,

		@Schema(description = "챌린지 종료 일시 (D-day 계산에 활용)", example = "2026-07-21T00:00:00")
		LocalDateTime endDate,

		@Schema(description = "카테고리 목록 (1~3개)")
		List<CategoryInfo> categories,

		@Schema(description = "팀 정보")
		TeamInfo team,

		@Schema(description = "팀장 여부 (true면 취소 버튼 노출)", example = "false")
		boolean isLeader,

		// ONGOING 전용 필드 (WAITING이면 null)
		@Schema(description = "현재 주차 번호 (ONGOING 전용, WAITING이면 null)", example = "1")
		Integer weekNumber,

		@Schema(description = "이번 주 목표 절약 금액 (ONGOING 전용, WAITING이면 null)", example = "300000")
		Integer weekGoalAmount,

		@Schema(description = "이번 주 팀 누적 절약 금액 (ONGOING 전용, WAITING이면 null)", example = "250000")
		Integer weekSavedAmount
	) {

		/**
		 * 카테고리 정보
		 * 챌린지 생성 시 선택한 카테고리와 해당 기준 금액을 담음
		 */
		@Schema(description = "카테고리 정보")
		public record CategoryInfo(

			@Schema(description = "카테고리 ID", example = "1")
			Long categoryId,

			@Schema(description = "카테고리 한국어 이름", example = "배달/외식")
			String categoryName,

			@Schema(description = "카테고리 아이콘 URL", example = "https://example.com/icon.png")
			String iconUrl,

			@Schema(description = "카테고리별 소비 기준 금액 (인증 시 실제 소비금액과 비교하여 절약 금액 산출: 기준 - 실제 소비)", example = "1500")
			int amount
		) {
			public static CategoryInfo from(ChallengeCategory cc) {
				return new CategoryInfo(
					cc.getCategory().getCategoryId(),
					cc.getCategory().getName().getDisplayName(),
					cc.getCategory().getIconUrl(),
					cc.getAmount()
				);
			}
		}

		/**
		 * 팀 정보
		 * inviteCode는 WAITING 상태에서 "초대코드 복사" 기능에 활용
		 */
		@Schema(description = "팀 정보")
		public record TeamInfo(

			@Schema(description = "팀 ID", example = "1")
			Long teamId,

			@Schema(description = "팀 이름", example = "절약왕팀")
			String teamName,

			@Schema(description = "팀 초대코드 (6자리)", example = "AB3K7Z")
			String inviteCode,

			@Schema(description = "현재 참여 인원", example = "3")
			int currentMemberCount,

			@Schema(description = "최대 참여 인원", example = "5")
			int maxMemberCount,

			@Schema(description = "팀 유형 한국어", example = "친구")
			String teamType
		) {
			public static TeamInfo from(Team team) {
				return new TeamInfo(
					team.getTeamId(),
					team.getTeamName(),
					team.getInviteCode(),
					team.getCurrentMemberCount(),
					team.getMaxMemberCount(),
					team.getType().getDisplayName()
				);
			}
		}

		/**
		 * WAITING 상태 응답 생성
		 * weekNumber, weekGoalAmount, weekSavedAmount는 null로 채움
		 */
		public static ChallengeInfo forWaiting(
			Challenge challenge,
			List<ChallengeCategory> categories,
			TeamRole userRole
		) {
			return new ChallengeInfo(
				challenge.getChallengeId(),
				challenge.getTitle(),
				challenge.getStatus().getDisplayName(),
				challenge.getGoalAmount(),
				challenge.getMinPersonalGoalAmount(),
				challenge.getStartDate(),
				challenge.getEndDate(),
				categories.stream().map(CategoryInfo::from).toList(),
				TeamInfo.from(challenge.getTeam()),
				userRole == TeamRole.LEADER,
				null, null, null
			);
		}

		/**
		 * ONGOING 상태 응답 생성
		 * 현재 주차(ChallengeWeek) 정보를 추가로 포함
		 */
		public static ChallengeInfo forOngoing(
			Challenge challenge,
			List<ChallengeCategory> categories,
			TeamRole userRole,
			ChallengeWeek currentWeek
		) {
			return new ChallengeInfo(
				challenge.getChallengeId(),
				challenge.getTitle(),
				challenge.getStatus().getDisplayName(),
				challenge.getGoalAmount(),
				challenge.getMinPersonalGoalAmount(),
				challenge.getStartDate(),
				challenge.getEndDate(),
				categories.stream().map(CategoryInfo::from).toList(),
				TeamInfo.from(challenge.getTeam()),
				userRole == TeamRole.LEADER,
				currentWeek.getWeekNumber(),
				currentWeek.getWeekGoalAmount(),
				currentWeek.getWeekSavedAmount()
			);
		}
	}

	// 활성 챌린지 없음 → challenge 필드 null 반환
	public static CurrentChallengeResponse none() {
		return new CurrentChallengeResponse("NONE", null);
	}

	// WAITING 상태 → forWaiting()으로 생성한 ChallengeInfo 반환
	public static CurrentChallengeResponse waiting(
		Challenge challenge,
		List<ChallengeCategory> categories,
		TeamRole userRole
	) {
		return new CurrentChallengeResponse(
			"WAITING",
			ChallengeInfo.forWaiting(challenge, categories, userRole)
		);
	}

	// ONGOING 상태 → forOngoing()으로 생성한 ChallengeInfo 반환 (주차 정보 포함)
	public static CurrentChallengeResponse ongoing(
		Challenge challenge,
		List<ChallengeCategory> categories,
		TeamRole userRole,
		ChallengeWeek currentWeek
	) {
		return new CurrentChallengeResponse(
			"ONGOING",
			ChallengeInfo.forOngoing(challenge, categories, userRole, currentWeek)
		);
	}
}
