package com.jjanpot.server.domain.auth.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjanpot.server.domain.auth.dto.request.DevLoginRequest;
import com.jjanpot.server.domain.auth.dto.response.LoginResponse;
import com.jjanpot.server.domain.auth.service.AuthService;
import com.jjanpot.server.domain.auth.service.ReviewModeService;
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
	private final ReviewModeService reviewModeService;

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
		summary = "심사 모드: 챌린지 즉시 시작",
		description = """
			**[심사 계정 전용]** WAITING 상태의 챌린지를 시간 조건 없이 즉시 ONGOING으로 전환합니다.

			**실행되는 작업 (1번 버튼 누르면 아래가 한 번에 처리됩니다):**
			1. 챌린지 상태 WAITING → ONGOING 전환
			2. 시작일/종료일을 현재 시각 기준으로 재설정 (종료일 = 지금으로부터 7일 후)
			3. 가짜 참가자 3명 자동 생성 및 팀 합류 (신고테스트유저A/B/C)
			4. 각 가짜 참가자의 신고용 가짜 인증 포스트 3개 자동 생성

			**앱 심사 시나리오:**
			- 로그인 응답의 `reviewMode: true`인 계정에서만 심사 버튼을 노출
			- 챌린지 생성 후 이 API 호출 → 즉시 진행 중 챌린지로 전환됨
			- 생성된 가짜 유저/포스트로 사용자 신고, 사용자 차단, 게시물 신고 기능 시연 가능

			**주의:** 챌린지가 이미 ONGOING/COMPLETED/FAILED 상태이면 400 에러 반환
			""")
	@ApiResponse(responseCode = "200", description = "챌린지 즉시 시작 + 가짜 데이터 생성 완료")
	@PostMapping("/review/challenge/{id}/start")
	public SuccessResponse<Void> reviewStartChallenge(@PathVariable Long id) {
		reviewModeService.startChallenge(id);
		return SuccessResponse.ok(null);
	}

	@Operation(
		summary = "심사 모드: 챌린지 즉시 종료",
		description = """
			**[심사 계정 전용]** ONGOING 상태의 챌린지를 즉시 종료하고 결과 데이터를 생성합니다.

			**실행되는 작업:**
			1. 챌린지 종료일을 현재 시각 - 1분으로 설정 (이미 종료된 것처럼 처리)
			2. 결과 생성 스케줄러 즉시 실행
			   - 팀 성공 여부 판정: 전원 개인 최소 금액 충족 AND 팀 공동 목표 금액 충족
			   - 챌린지 상태 → COMPLETED (성공) 또는 FAILED (실패)
			   - challenge_member_result, challenge_team_result 데이터 생성

			**앱 심사 시나리오:**
			- 즉시 시작 후 결과 화면까지 한 번에 시연하고 싶을 때 사용
			- 종료 후 `GET /api/challenges/v1/{id}/result` 로 결과 조회 가능

			**주의:** 챌린지가 ONGOING 상태가 아니면 400 에러 반환
			""")
	@ApiResponse(responseCode = "200", description = "챌린지 즉시 종료 및 결과 생성 완료")
	@PostMapping("/review/challenge/{id}/finish")
	public SuccessResponse<Void> reviewFinishChallenge(@PathVariable Long id) {
		reviewModeService.finishChallenge(id);
		return SuccessResponse.ok(null);
	}

	@Operation(
		summary = "심사 모드: 가짜 참가자 + 신고용 포스트 수동 생성",
		description = """
			**[심사 계정 전용]** 챌린지에 가짜 참가자 3명과 신고용 가짜 인증 포스트 3개를 수동으로 생성합니다.

			> ⚠️ 이 API는 보통 직접 호출할 필요 없습니다.
			> `/review/challenge/{id}/start` 호출 시 자동으로 실행됩니다.
			> 가짜 데이터만 별도로 추가해야 하는 경우에만 사용하세요.

			**생성되는 데이터:**
			- 가짜 유저: 신고테스트유저A / B / C (이미 존재하면 재사용, 멱등)
			- 가짜 인증 포스트: 각 유저당 1개씩 총 3개 (신고/차단 시나리오용 메모 포함)
			- 가짜 유저들은 해당 챌린지 팀에 자동으로 합류됨

			**활용 시나리오:**
			- 사용자 신고: 가짜 유저를 신고
			- 사용자 차단: 가짜 유저를 차단
			- 게시물 신고: 가짜 인증 포스트를 신고
			""")
	@ApiResponse(responseCode = "200", description = "가짜 데이터 생성 완료")
	@PostMapping("/review/challenge/{id}/seed")
	public SuccessResponse<Void> reviewSeedFakeData(@PathVariable Long id) {
		reviewModeService.seedFakeData(id);
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
