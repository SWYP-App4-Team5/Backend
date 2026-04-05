package com.jjanpot.server.domain.user.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjanpot.server.domain.user.controller.docs.MyPageControllerDocs;
import com.jjanpot.server.domain.user.dto.response.ChallengeStatsResponse;
import com.jjanpot.server.domain.user.dto.response.UserProfileResponse;
import com.jjanpot.server.domain.user.service.UserService;
import com.jjanpot.server.global.annotation.CurrentUserId;
import com.jjanpot.server.global.common.dto.SuccessResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users/v1")
public class MyPageController implements MyPageControllerDocs {

	private final UserService userService;

	@GetMapping("/challenge-stats")
	public SuccessResponse<ChallengeStatsResponse> getChallengeStats(@CurrentUserId Long userId) {
		return SuccessResponse.ok(userService.getChallengeStats(userId));
	}

	@GetMapping("/profile")
	public SuccessResponse<UserProfileResponse> getProfile(@CurrentUserId Long userId) {
		return SuccessResponse.ok(userService.getProfile(userId));
	}

	@DeleteMapping("/withdraw")
	public SuccessResponse<Void> withdraw(@CurrentUserId Long userId) {
		userService.withdraw(userId);
		return SuccessResponse.ok(null);
	}
}
