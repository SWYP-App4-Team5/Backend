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
		description = """
			챌린지 상태 자동 전환 스케줄러를 즉시 실행합니다. (원래 매일 자정 KST에 자동 실행)
			
			**실행되는 작업:**
			1. WAITING → ONGOING 전환: 시작일(start_date)이 지난 대기 중 챌린지를 진행 중으로 변경
			2. ONGOING → COMPLETED/FAILED 전환: 종료일(end_date)이 지난 진행 중 챌린지를 종료 처리
			   - 팀/개인 결과 데이터(challenge_team_result, challenge_member_result) 자동 생성
			   - 팀 성공 조건: 개인 최소 금액 충족 AND 팀 공동 목표 금액 충족
			
			**테스트 방법 (챌린지 종료 테스트):**
			1. DB에서 챌린지의 end_date를 과거로 변경 (예: 직접 값 수정 or 쿼리 작성 `UPDATE challenge SET end_date = '2026-04-04 00:00:00' WHERE challenge_id = 1`)
			2. 이 API를 호출하면 스케줄러가 해당 챌린지를 COMPLETED 또는 FAILED로 전환
			3. `GET /api/challenges/v1/{id}/result`로 결과 조회 가능
			
			**주의:** **DB에서 status를 직접 COMPLETED로 변경하면 결과 데이터가 생성되지 않으므로, 반드시 이 스케줄러를 통해 종료시켜야 합니다.**
			""")
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
