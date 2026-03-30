package com.jjanpot.server.domain.challenge.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjanpot.server.domain.challenge.controller.docs.ChallengeControllerDocs;
import com.jjanpot.server.domain.challenge.dto.request.CreateChallengeRequest;
import com.jjanpot.server.domain.challenge.dto.response.ChallengeDetailResponse;
import com.jjanpot.server.domain.challenge.dto.response.ChallengeMembersResponse;
import com.jjanpot.server.domain.challenge.dto.response.ChallengeStatsResponse;
import com.jjanpot.server.domain.challenge.dto.response.CreateChallengeResponse;
import com.jjanpot.server.domain.challenge.dto.response.CurrentChallengeResponse;
import com.jjanpot.server.domain.challenge.service.ChallengeService;
import com.jjanpot.server.global.annotation.CurrentUserId;
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
		@CurrentUserId Long userId,
		@Valid @RequestBody CreateChallengeRequest request
	) {
		CreateChallengeResponse response = challengeService.createChallenge(userId, request);
		return SuccessResponse.created(response);
	}

	// 챌린지 취소하기
	@PostMapping("/{id}/cancel")
	public SuccessResponse<Void> cancelChallenge(
		@CurrentUserId Long userId,
		@PathVariable Long id
	) {
		challengeService.cancelChallenge(userId, id);
		return SuccessResponse.ok(null);
	}

	// 챌린지 상세보기
	@GetMapping("/{id}/detail")
	public SuccessResponse<ChallengeDetailResponse> getChallengeDetail(
		@CurrentUserId Long userId,
		@PathVariable Long id
	) {
		ChallengeDetailResponse response = challengeService.getChallengeDetail(userId, id);
		return SuccessResponse.ok(response);
	}

	// 홈 화면
	// 챌린지 대시보드
	@GetMapping("/current")
	public SuccessResponse<CurrentChallengeResponse> getCurrentChallenge(
		@CurrentUserId Long userId
	) {
		CurrentChallengeResponse response = challengeService.getCurrentChallenge(userId);
		return SuccessResponse.ok(response);
	}

	// 챌린지 팀원 절약 현황 조회 (챌린지 정보 화면)
	@GetMapping("/{id}/members")
	public SuccessResponse<ChallengeMembersResponse> getChallengeMembers(
		@CurrentUserId Long userId,
		@PathVariable Long id
	) {
		ChallengeMembersResponse response = challengeService.getChallengeMembers(userId, id);
		return SuccessResponse.ok(response);
	}

	// 팀/개인 절약 현황 통계 (홈화면 스크롤 시)
	@GetMapping("/{id}/stats")
	public SuccessResponse<ChallengeStatsResponse> getChallengeStats(
		@CurrentUserId Long userId,
		@PathVariable Long id
	) {
		ChallengeStatsResponse response = challengeService.getChallengeStats(userId, id);
		return SuccessResponse.ok(response);
	}
}
