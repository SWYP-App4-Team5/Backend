package com.jjanpot.server.domain.team.controller.docs;

import com.jjanpot.server.domain.team.dto.JoinTeamRequest;
import com.jjanpot.server.domain.team.dto.JoinTeamResponse;
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

@Tag(name = "Team", description = "팀 API")
@SecurityRequirement(name = "bearerAuth")
public interface TeamControllerV1Docs {

	@Operation(
		summary = "팀 참여 (초대 코드 입력)",
		description = """
			초대 코드로 팀에 참여하는 API입니다.

			- 6자리 초대 코드를 입력하여 팀에 참여합니다.
			- 챌린지가 WAITING 상태일 때만 참여 가능합니다.
			- 이미 시작된 챌린지의 팀에는 참여할 수 없습니다.
			- 팀 정원이 초과된 경우 참여할 수 없습니다.
			"""
	)
	@ApiResponse(responseCode = "201", description = "팀 참여 성공")
	SuccessResponse<JoinTeamResponse> joinTeam(
		@Parameter(hidden = true) User user,

		@RequestBody(
			description = "팀 참여 요청",
			content = @Content(
				schema = @Schema(implementation = JoinTeamRequest.class),
				examples = @ExampleObject(
					name = "초대 코드 입력",
					value = """
						{
						  "inviteCode": "AB3K9P"
						}
						"""
				)
			)
		)
		JoinTeamRequest request
	);
}
