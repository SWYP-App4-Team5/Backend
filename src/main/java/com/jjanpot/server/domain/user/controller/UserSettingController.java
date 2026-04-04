package com.jjanpot.server.domain.user.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjanpot.server.domain.user.controller.docs.UserControllerDocs;
import com.jjanpot.server.domain.user.dto.request.InviteCodeRequest;
import com.jjanpot.server.domain.user.dto.request.ProfileCreateRequest;
import com.jjanpot.server.domain.user.dto.request.UserAgreementRequest;
import com.jjanpot.server.domain.user.dto.response.InviteCodeResponse;
import com.jjanpot.server.domain.user.dto.response.ProfileCreateResponse;
import com.jjanpot.server.domain.user.service.UserService;
import com.jjanpot.server.global.annotation.CurrentUserId;
import com.jjanpot.server.global.common.dto.SuccessResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(("/api/users/v1/onboarding"))
public class UserSettingController implements UserControllerDocs {

	private final UserService userService;

	@PostMapping("/profile")
	public SuccessResponse<ProfileCreateResponse> onboardCreateProfile(
		@Valid @RequestBody ProfileCreateRequest request,
		@CurrentUserId Long userId
	) {
		ProfileCreateResponse profileCreateResponse = userService.onboardingCreateProfile(request, userId);
		return SuccessResponse.ok(profileCreateResponse);
	}

	@PostMapping("/agreement")
	public SuccessResponse<Void> agreeToTerms(
		@Valid @RequestBody UserAgreementRequest request,
		@CurrentUserId Long userId
	) {
		userService.agreeToTerms(userId, request);
		return SuccessResponse.ok(null);
	}

	@PostMapping("/invite-code")
	public SuccessResponse<InviteCodeResponse> inputInviteCode(
		@Valid @RequestBody InviteCodeRequest request,
		@CurrentUserId Long userId
	) {
		InviteCodeResponse response = userService.joinChallengeByInviteCode(
			request.inviteCode(),
			userId
		);
		return SuccessResponse.ok(response);
	}
}
