package com.jjanpot.server.domain.challenge.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjanpot.server.domain.challenge.controller.docs.ChallengeControllerDocs;
import com.jjanpot.server.domain.challenge.dto.request.CreateChallengeRequest;
import com.jjanpot.server.domain.challenge.dto.response.ChallengeDetailResponse;
import com.jjanpot.server.domain.challenge.dto.response.ChallengeStatsResponse;
import com.jjanpot.server.domain.challenge.dto.response.CreateChallengeResponse;
import com.jjanpot.server.domain.challenge.dto.response.CurrentChallengeResponse;
import com.jjanpot.server.domain.challenge.service.ChallengeService;
import com.jjanpot.server.domain.user.entity.User;
import com.jjanpot.server.global.common.dto.SuccessResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/challenges/v1")
public class ChallengeController implements ChallengeControllerDocs {

	private final ChallengeService challengeService;

	// 챌린지 생성하기
	@PostMapping
	public SuccessResponse<CreateChallengeResponse> createChallenge(
		@AuthenticationPrincipal User user,
		@Valid @RequestBody CreateChallengeRequest request
	) {
		CreateChallengeResponse response = challengeService.createChallenge(user, request);
		return SuccessResponse.created(response);
	}

	// 챌린지 취소하기
	@PostMapping("/{id}/cancel")
	public SuccessResponse<Void> cancelChallenge(
		@AuthenticationPrincipal User user,
		@PathVariable Long id
	) {
		challengeService.cancelChallenge(user, id);
		return SuccessResponse.ok(null);

	}

	// 챌린지 상세보기
	@GetMapping("/{id}/detail")
	public SuccessResponse<ChallengeDetailResponse> getChallengeDetail(
		@AuthenticationPrincipal User user,
		@PathVariable Long id
	) {
		ChallengeDetailResponse response = challengeService.getChallengeDetail(user, id);
		return SuccessResponse.ok(response);
	}

	// 홈 화면
	// 챌린지 대시보드
	@GetMapping("/current")
	public SuccessResponse<CurrentChallengeResponse> getCurrentChallenge(
		@AuthenticationPrincipal User user
	) {
		CurrentChallengeResponse response = challengeService.getCurrentChallenge(user);
		return SuccessResponse.ok(response);
	}

	// TODO: 인증(certification) 도메인 구현 후 로직 작성
	// 팀/개인 절약 현황 통계 (홈화면 스크롤 시)
	@GetMapping("/{id}/stats")
	public SuccessResponse<ChallengeStatsResponse> getChallengeStats(
		@AuthenticationPrincipal User user,
		@PathVariable Long id
	) {
		return SuccessResponse.ok(challengeService.getChallengeStats(user, id));
	}
}
