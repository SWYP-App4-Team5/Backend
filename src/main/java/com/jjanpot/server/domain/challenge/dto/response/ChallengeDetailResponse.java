package com.jjanpot.server.domain.challenge.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.challenge.entity.ChallengeCategory;
import com.jjanpot.server.domain.team.entity.Team;
import com.jjanpot.server.domain.team.entity.TeamRole;

import io.swagger.v3.oas.annotations.media.Schema;

// 챌린지 상세보기 화면
@Schema(description = "챌린지 상세 조회 응답")
public record ChallengeDetailResponse(

	@Schema(description = "챌린지 ID", example = "1")
	Long challengeId,

	@Schema(description = "챌린지 제목", example = "배달을 아껴요")
	String title,

	@Schema(description = "챌린지 설명", example = "배달음식 줄이고 같이 절약해봐요!")
	String description,

	@Schema(description = "챌린지 상태", example = "대기중인 챌린지")
	String status,

	@Schema(description = "팀 전체 목표 절약 금액", example = "300000")
	int goalAmount,

	@Schema(description = "인당 최소 목표 절약 금액", example = "20000")
	int minPersonalGoalAmount,

	@Schema(description = "챌린지 시작 일시", example = "2026-07-15T00:00:00")
	LocalDateTime startDate,

	@Schema(description = "챌린지 종료 일시", example = "2026-07-21T00:00:00")
	LocalDateTime endDate,

	@Schema(description = "카테고리 목록 (1~3개)")
	List<CategoryInfo> categories,

	@Schema(description = "팀 정보")
	TeamInfo team,

	@Schema(description = "팀장 여부 (true면 취소 버튼 노출)", example = "true")
	boolean isLeader
) {

	// 챌린지 정보
	@Schema(description = "카테고리 정보")
	public record CategoryInfo(

		@Schema(description = "카테고리 ID", example = "1")
		Long categoryId,

		@Schema(description = "카테고리 한국어 이름", example = "배달/외식")
		String categoryName,

		@Schema(description = "카테고리 아이콘 URL", example = "https://example.com/icon.png")
		String iconUrl,

		@Schema(description = "카테고리별 소비 기준 금액 (인증 시 실제 소비금액과 비교하여 절약 금액 산출: 기준 - 실제 소비)", example = "15000")
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

	// 팀 정보
	@Schema(description = "팀 정보")
	public record TeamInfo(

		@Schema(description = "팀 ID", example = "1")
		Long teamId,

		@Schema(description = "팀 초대코드 (6자리)", example = "AB3K7Z")
		String inviteCode,

		@Schema(description = "현재 참여 인원", example = "3")
		int currentMemberCount,

		@Schema(description = "최대 참여 인원", example = "5")
		int maxMemberCount,

		@Schema(description = "팀 유형", example = "연인")
		String teamType
	) {
		public static TeamInfo from(Team team) {
			return new TeamInfo(
				team.getTeamId(),
				team.getInviteCode(),
				team.getCurrentMemberCount(),
				team.getMaxMemberCount(),
				team.getType().getDisplayName()
			);
		}
	}

	public static ChallengeDetailResponse from(
		Challenge challenge,
		List<ChallengeCategory> categories,
		TeamRole userRole
	) {
		return new ChallengeDetailResponse(
			challenge.getChallengeId(),
			challenge.getTitle(),
			challenge.getDescription(),
			challenge.getStatus().getDisplayName(),
			challenge.getGoalAmount(),
			challenge.getMinPersonalGoalAmount(),
			challenge.getStartDate(),
			challenge.getEndDate(),
			categories.stream().map(CategoryInfo::from).toList(),
			TeamInfo.from(challenge.getTeam()),
			userRole == TeamRole.LEADER
		);
	}
}
