package com.jjanpot.server.domain.challenge.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjanpot.server.domain.challenge.controller.docs.ChallengeControllerDocs;
import com.jjanpot.server.domain.challenge.dto.CreateChallengeRequest;
import com.jjanpot.server.domain.challenge.dto.CreateChallengeResponse;
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

	@PostMapping
	public SuccessResponse<CreateChallengeResponse> createChallenge(
		@AuthenticationPrincipal User user,
		@Valid @RequestBody CreateChallengeRequest request
	) {
		CreateChallengeResponse response = challengeService.createChallenge(user, request);
		return SuccessResponse.created(response);
	}
}
