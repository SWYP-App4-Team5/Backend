package com.jjanpot.server.domain.user.controller.docs;

import com.jjanpot.server.domain.user.dto.response.ChallengeStatsResponse;
import com.jjanpot.server.domain.user.dto.response.UserProfileResponse;
import com.jjanpot.server.global.common.dto.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User - MyPage", description = "마이페이지 API")
@SecurityRequirement(name = "JWT TOKEN")
public interface MyPageControllerDocs {

	@Operation(summary = "챌린지 통계 조회", description = "로그인한 사용자의 총 챌린지 수, 성공/실패 수, 성공률을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "챌린지 통계 조회 성공")
	SuccessResponse<ChallengeStatsResponse> getChallengeStats(@Parameter(hidden = true) Long userId);

	@Operation(summary = "프로필 조회", description = "로그인한 사용자의 프로필 정보를 조회합니다.")
	@ApiResponse(responseCode = "200", description = "프로필 조회 성공")
	SuccessResponse<UserProfileResponse> getProfile(@Parameter(hidden = true) Long userId);

	@Operation(
		summary = "회원 탈퇴",
		description = """
			회원 탈퇴 API입니다.

			- 진행 중(WAITING/ONGOING)인 챌린지가 있으면 탈퇴 불가 (400 에러)
			- 탈퇴 시 유저와 연관된 모든 데이터가 영구 삭제됩니다:
			  인증, 좋아요, 챌린지 결과, 알림, 팀 멤버십, 디바이스, 약관 동의, 토큰
			- 삭제된 계정은 복구할 수 없습니다.
			"""
	)
	@ApiResponse(responseCode = "200", description = "회원 탈퇴 성공")
	SuccessResponse<Void> withdraw(@Parameter(hidden = true) Long userId);
}
