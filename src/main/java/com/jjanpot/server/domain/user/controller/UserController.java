package com.jjanpot.server.domain.user.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjanpot.server.domain.user.dto.InviteCodeRequest;
import com.jjanpot.server.domain.user.dto.InviteCodeResponse;
import com.jjanpot.server.domain.user.dto.ProfileCreateRequest;
import com.jjanpot.server.domain.user.dto.ProfileCreateResponse;
import com.jjanpot.server.domain.user.service.UserService;
import com.jjanpot.server.global.annotation.CurrentUserId;
import com.jjanpot.server.global.common.dto.SuccessResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(("/api/users/v1/onboarding"))
public class UserController {

	private final UserService userService;

	@PostMapping("/profile")
	public SuccessResponse<ProfileCreateResponse> onboardCreateProfile(@Valid @RequestBody ProfileCreateRequest request,
		@CurrentUserId Long userId) {
		ProfileCreateResponse profileCreateResponse = userService.onboardingCreateProfile(request, userId);
		return SuccessResponse.ok(profileCreateResponse);
	}

	@PostMapping("/invite-code")
	public SuccessResponse<InviteCodeResponse> inputInviteCode(
		@Valid @RequestBody InviteCodeRequest request,
		@CurrentUserId Long userId
	) {
		InviteCodeResponse response = userService.joinChallengeByInviteCode(
			request.code(),
			userId
		);
		return SuccessResponse.ok(response);
	}
}
