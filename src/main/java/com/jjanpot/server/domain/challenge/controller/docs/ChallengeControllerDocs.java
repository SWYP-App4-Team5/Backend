package com.jjanpot.server.domain.challenge.controller.docs;

import com.jjanpot.server.domain.challenge.dto.CreateChallengeRequest;
import com.jjanpot.server.domain.challenge.dto.CreateChallengeResponse;
import com.jjanpot.server.domain.user.entity.User;
import com.jjanpot.server.global.common.dto.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Challenge", description = "챌린지 API")
@SecurityRequirement(name = "bearerAuth")
public interface ChallengeControllerDocs {

	@Operation(
		summary = "챌린지 생성",
		description = """
			챌린지 생성 API입니다.
			
			로그인한 유저가 팀을 구성하여 챌린지를 생성합니다.
			- 챌린지 생성자는 자동으로 팀장(LEADER)이 됩니다.
			- 팀 생성과 동시에 초대 코드가 발급됩니다.
			- 카테고리는 최소 1개, 최대 3개까지 선택 가능합니다.
			- 최대 참여 인원은 2명 ~ 8명 사이로 설정해야 합니다.
			- 챌린지는 시작일로부터 7일간 진행됩니다.
			- 팀 전체 목표 금액은 인원수별 최소 금액 정책을 충족해야 합니다.
			"""
	)
	@ApiResponse(responseCode = "200", description = "챌린지 생성 성공")
	SuccessResponse<CreateChallengeResponse> createChallenge(
		@Parameter(hidden = true) User user,

		@RequestBody(
			description = "챌린지 생성 요청",
			content = @Content(
				schema = @Schema(implementation = CreateChallengeRequest.class),
				examples = {
					@ExampleObject(
						name = "카테고리 1개 선택",
						value = """
							{
							  "teamName": "절약왕팀",
							  "teamType": "FRIEND",
							  "maxMemberCount": 4,
							  "startDate": "2026-03-18",
							  "categories": [
							    { "categoryId": 1, "amount": 50000 }
							  ],
							  "goalAmount": 200000,
							  "minPersonalGoalAmount": 30000
							}
							"""
					),
					@ExampleObject(
						name = "카테고리 3개 선택 (최대)",
						value = """
							{
							  "teamName": "절약왕팀",
							  "teamType": "FRIEND",
							  "maxMemberCount": 4,
							  "startDate": "2026-03-18",
							  "categories": [
							    { "categoryId": 1, "amount": 50000 },
							    { "categoryId": 2, "amount": 30000 },
							    { "categoryId": 4, "amount": 20000 }
							  ],
							  "goalAmount": 400000,
							  "minPersonalGoalAmount": 30000
							}
							"""
					)
				}
			)
		)
		CreateChallengeRequest request
	);
}
