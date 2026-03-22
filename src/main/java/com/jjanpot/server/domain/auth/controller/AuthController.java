package com.jjanpot.server.domain.auth.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjanpot.server.domain.auth.controller.docs.AuthControllerV1Docs;
import com.jjanpot.server.domain.auth.dto.LoginRequest;
import com.jjanpot.server.domain.auth.dto.LoginResponse;
import com.jjanpot.server.domain.auth.dto.RefreshRequest;
import com.jjanpot.server.domain.auth.dto.RefreshResponse;
import com.jjanpot.server.domain.auth.service.AuthService;
import com.jjanpot.server.domain.user.entity.Provider;
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
		LoginResponse response = authService.login(Provider.from(provider), request.accessToken());
		return SuccessResponse.ok(response);
	}

	@PostMapping("/refresh")
	public SuccessResponse<RefreshResponse> refresh(@Valid @RequestBody RefreshRequest request) {
		RefreshResponse refreshResponse = authService.refreshToken(request.refreshToken());
		return SuccessResponse.ok(refreshResponse);
	}
}
