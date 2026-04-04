package com.jjanpot.server.domain.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjanpot.server.domain.auth.dto.request.DevLoginRequest;
import com.jjanpot.server.domain.auth.dto.response.LoginResponse;
import com.jjanpot.server.domain.auth.service.AuthService;
import com.jjanpot.server.domain.challenge.scheduler.ChallengeScheduler;
import com.jjanpot.server.domain.user.entity.Provider;
import com.jjanpot.server.global.common.dto.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "dev-Auth", description = "로컬 개발용 인증 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth/v1")
public class DevAuthController {

	private final AuthService authService;
	private final ChallengeScheduler challengeScheduler;

	@Operation(summary = "스케줄러 수동 실행 (챌린지 상태 전환 + 결과 생성)",
		description = "WAITING→ONGOING 전환과 ONGOING→COMPLETED/FAILED 전환을 즉시 실행합니다.")
	@ApiResponse(responseCode = "200", description = "스케줄러 실행 완료")
	@PostMapping("/run-scheduler")
	public SuccessResponse<Void> runScheduler() {
		challengeScheduler.transitionWaitingToOngoing();
		challengeScheduler.transitionOngoingToFinished();
		return SuccessResponse.ok(null);
	}

	@Operation(
		summary = "로컬 개발용 로그인",
		description = """
			local 프로필에서만 활성화되는 개발용 로그인 API입니다.
			
			카카오 로그인 없이 테스트 유저를 생성하거나 재사용하고 JWT를 발급합니다.
			Swagger Authorize에 accessToken을 넣어 다른 API를 테스트할 때 사용합니다.
			"""
	)
	@ApiResponse(responseCode = "200", description = "개발용 로그인 성공")
	@PostMapping("/dev-login")
	public SuccessResponse<LoginResponse> devLogin(@Valid @RequestBody DevLoginRequest request) {
		LoginResponse response = authService.devLogin(
			Provider.from(request.provider()),
			request.providerId(),
			request.nickname(),
			request.email(),
			request.profileImageUrl()
		);
		return SuccessResponse.ok(response);
	}
}
