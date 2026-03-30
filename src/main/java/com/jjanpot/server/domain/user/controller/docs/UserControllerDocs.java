package com.jjanpot.server.domain.user.controller.docs;

import com.jjanpot.server.domain.user.dto.request.InviteCodeRequest;
import com.jjanpot.server.domain.user.dto.request.ProfileCreateRequest;
import com.jjanpot.server.domain.user.dto.request.UserAgreementRequest;
import com.jjanpot.server.domain.user.dto.response.InviteCodeResponse;
import com.jjanpot.server.domain.user.dto.response.ProfileCreateResponse;
import com.jjanpot.server.global.common.dto.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User - Onboarding", description = "온보딩 API (약관 동의, 프로필 생성, 초대코드 참여)")
@SecurityRequirement(name = "bearerAuth")
public interface UserControllerDocs {

	@Operation(
		summary = "약관 동의",
		description = """
			온보딩 과정에서 필수 약관에 동의합니다.

			- 만 14세 이상 확인, 이용약관 동의, 개인정보처리방침 동의는 필수입니다. (모두 true)
			- 마케팅 수신 동의는 선택이며, 미입력 시 false로 처리됩니다.
			- 이미 약관 동의를 완료한 사용자는 중복 요청 시 400 에러가 발생합니다.
			"""
	)
	@ApiResponse(responseCode = "200", description = "약관 동의 성공")
	SuccessResponse<Void> agreeToTerms(
		UserAgreementRequest request,
		@Parameter(hidden = true) Long userId
	);

	@Operation(
		summary = "프로필 생성",
		description = """
			온보딩 과정에서 사용자 프로필을 생성합니다.

			- 닉네임과 생년월일은 필수 입력입니다.
			- 프로필 이미지 URL은 선택 입력입니다.
			"""
	)
	@ApiResponse(responseCode = "200", description = "프로필 생성 성공")
	SuccessResponse<ProfileCreateResponse> onboardCreateProfile(
		ProfileCreateRequest request,
		@Parameter(hidden = true) Long userId
	);

	@Operation(
		summary = "초대코드 입력",
		description = """
			온보딩 과정에서 초대코드를 입력하여 팀에 참여합니다.

			- 6자리 초대코드로 팀을 찾아 참여합니다.
			- 대기 중(WAITING) 상태의 챌린지가 있는 팀에만 참여 가능합니다.
			- 이미 팀원인 경우 또는 정원이 초과된 경우 참여할 수 없습니다.
			"""
	)
	@ApiResponse(responseCode = "200", description = "팀 참여 성공")
	SuccessResponse<InviteCodeResponse> inputInviteCode(
		InviteCodeRequest request,
		@Parameter(hidden = true) Long userId
	);
}
