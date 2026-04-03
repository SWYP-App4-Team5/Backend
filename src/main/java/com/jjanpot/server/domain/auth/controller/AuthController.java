package com.jjanpot.server.domain.auth.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjanpot.server.domain.auth.controller.docs.AuthControllerV1Docs;
import com.jjanpot.server.domain.auth.dto.request.CodeLoginRequest;
import com.jjanpot.server.domain.auth.dto.request.LoginRequest;
import com.jjanpot.server.domain.auth.dto.request.RefreshRequest;
import com.jjanpot.server.domain.auth.dto.response.LoginResponse;
import com.jjanpot.server.domain.auth.dto.response.RefreshResponse;
import com.jjanpot.server.domain.auth.service.AuthService;
import com.jjanpot.server.domain.user.entity.Provider;
import com.jjanpot.server.global.annotation.CurrentUserId;
import com.jjanpot.server.global.common.dto.SuccessResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth/v1")
public class AuthController implements AuthControllerV1Docs {

	private final AuthService authService;

	@PostMapping("/login/{provider}")
	public SuccessResponse<LoginResponse> login(@PathVariable String provider,
		@Valid @RequestBody LoginRequest request) {
		LoginResponse response = authService.login(Provider.from(provider), request);
		return SuccessResponse.ok(response);
	}

	@PostMapping("/refresh")
	public SuccessResponse<RefreshResponse> refresh(@Valid @RequestBody RefreshRequest request) {
		RefreshResponse refreshResponse = authService.refreshToken(request.refreshToken());
		return SuccessResponse.ok(refreshResponse);
	}

	@PostMapping("/login/kakao/code")
	public SuccessResponse<LoginResponse> loginWithCode(@Valid @RequestBody CodeLoginRequest request) {
		LoginResponse response = authService.loginWithCode(
			request.code(), request.redirectUri(), request.deviceUuid(), request.fcmToken());
		return SuccessResponse.ok(response);
	}

	@PostMapping("/logout")
	public SuccessResponse<Void> logout(@CurrentUserId Long userId) {
		authService.logout(userId);
		return SuccessResponse.ok(null);
	}
}
