package com.jjanpot.server.domain.challenge.controller.docs;

import com.jjanpot.server.domain.challenge.dto.request.CreateChallengeRequest;
import com.jjanpot.server.domain.challenge.dto.response.ChallengeDetailResponse;
import com.jjanpot.server.domain.challenge.dto.response.ChallengeStatsResponse;
import com.jjanpot.server.domain.challenge.dto.response.CreateChallengeResponse;
import com.jjanpot.server.domain.challenge.dto.response.CurrentChallengeResponse;
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
			
			categories[].amount: 카테고리별 소비 기준 금액입니다.
			- GET /api/categories/v1 의 amountOptions 중 선택하거나 직접 입력할 수 있습니다.
			- 인증 시 실제 소비금액과 비교하여 절약 금액을 산출합니다. (절약 금액 = 기준 - 실제 소비)
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
							  "title": "카페는 이제 그만!",
							  "description": "카페를 줄이고 싶은 사람들을 위한 방입니다! 우리 모두 절약을 함께 해요!",
							  "teamType": "FRIEND",
							  "maxMemberCount": 4,
							  "startDate": "2026-03-18",
							  "categories": [
							    { "categoryId": 1, "amount": 15000 }
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
							  "title": "우리 팀 절약 챌린지",
							  "teamType": "CLUB",
							  "maxMemberCount": 4,
							  "startDate": "2026-03-18",
							  "categories": [
							    { "categoryId": 1, "amount": 1500 },
							    { "categoryId": 2, "amount": 10000 },
							    { "categoryId": 4, "amount": 8000 }
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

	@Operation(
		summary = "챌린지 취소",
		description = """
			챌린지를 취소합니다.
			
			- 팀장(LEADER)만 취소할 수 있습니다.
			- WAITING(대기 중) 상태의 챌린지만 취소 가능합니다.
			"""
	)
	@ApiResponse(responseCode = "200", description = "챌린지 취소 성공")
	SuccessResponse<Void> cancelChallenge(
		@Parameter(hidden = true) User user,
		@Parameter(description = "챌린지 ID") Long id
	);

	@Operation(
		summary = "챌린지 상세 조회",
		description = """
			챌린지 상세 정보를 조회합니다.
			
			- 해당 챌린지의 팀원만 조회할 수 있습니다.
			- isLeader가 true이면 취소 버튼 등 팀장 전용 기능을 노출합니다.
			- categories[].amount: 카테고리별 소비 기준 금액입니다.
			  인증 시 실제 소비금액과 비교하여 절약 금액을 산출합니다. (절약 금액 = 기준 - 실제 소비)
			"""
	)
	@ApiResponse(responseCode = "200", description = "챌린지 상세 조회 성공")
	SuccessResponse<ChallengeDetailResponse> getChallengeDetail(
		@Parameter(hidden = true) User user,
		@Parameter(description = "챌린지 ID") Long id
	);

	@Operation(
		summary = "팀/개인 절약 현황 통계 조회 (홈화면)",
		description = """
			홈화면 스크롤 시 노출되는 팀/개인 절약 현황을 조회합니다.
			
			- 팀 절약 현황: 인증평균, 참여율, 연속활동일
			- 개인 절약 현황: 인증횟수, 참여율, 연속활동일
			- ONGOING 상태의 챌린지에서만 유효합니다.
			
			※ 인증(certification) 도메인 구현 후 응답 데이터가 채워집니다.
			"""
	)
	@ApiResponse(responseCode = "200", description = "절약 현황 조회 성공")
	SuccessResponse<ChallengeStatsResponse> getChallengeStats(
		@Parameter(hidden = true) User user,
		@Parameter(description = "챌린지 ID") Long id
	);

	@Operation(
		summary = "현재 챌린지 조회 (홈 화면)",
		description = """
			로그인한 유저의 현재 활성 챌린지를 조회합니다.
			
			status 필드에 따라 3가지 상태로 분기됩니다:
			- NONE: 활성 챌린지 없음 (waiting, ongoing 필드 모두 null)
			- WAITING: 시작일 전 대기 중인 챌린지
			- ONGOING: 진행 중인 챌린지 (weekNumber, weekGoalAmount, weekSavedAmount 포함)
			"""
	)
	@ApiResponse(responseCode = "200", description = "현재 챌린지 조회 성공")
	SuccessResponse<CurrentChallengeResponse> getCurrentChallenge(
		@Parameter(hidden = true) User user
	);
}
