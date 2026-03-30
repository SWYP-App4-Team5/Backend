package com.jjanpot.server.domain.challenge.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "챌린지 팀원 절약 현황 응답")
public record ChallengeMembersResponse(

	@Schema(description = "챌린지 ID", example = "1")
	Long challengeId,

	@Schema(description = "챌린지 제목", example = "카페는 이제 그만!")
	String title,

	@Schema(description = "챌린지 시작일", example = "2026-03-25T00:00:00")
	String startDate,

	@Schema(description = "현재까지 팀 전체 절약 금액", example = "261000")
	int totalSavedAmount,

	@Schema(description = "팀 목표 금액", example = "300000")
	int goalAmount,

	@Schema(description = "팀원별 절약 현황 목록")
	List<MemberSavingInfo> members
) {

	@Schema(description = "팀원 절약 정보")
	public record MemberSavingInfo(

		@Schema(description = "유저 ID", example = "1")
		Long userId,

		@Schema(description = "닉네임", example = "오므라이스최고")
		String nickname,

		@Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg", nullable = true)
		String profileImageUrl,

		@Schema(description = "개인 절약 금액", example = "36500")
		int savedAmount,

		@Schema(description = "본인 여부", example = "false")
		boolean isMe
	) {
	}
}
