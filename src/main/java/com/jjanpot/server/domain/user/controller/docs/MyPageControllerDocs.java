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
}
