package com.jjanpot.server.domain.notification.controller;

import org.springframework.web.bind.annotation.PathVariable;

import com.jjanpot.server.global.common.dto.ErrorResponse;
import com.jjanpot.server.global.common.dto.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Notification", description = "알림 관련 API")
@SecurityRequirement(name = "bearerAuth")
public interface NotificationFacade {
	@Operation(
		summary = "알림 읽음 처리",
		description = "사용자가 특정 알림을 확인했을 때 읽음 상태로 변경합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "읽음 처리 성공",
			content = @Content(schema = @Schema(implementation = SuccessResponse.class))
		),
		@ApiResponse(
			responseCode = "401",
			description = "해당 사용자의 알림이 아님",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
		),
		@ApiResponse(
			responseCode = "404",
			description = "존재하지 않는 알림",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
		)

	})
	SuccessResponse<Long> read(
		@Parameter(hidden = true) Long userId,
		@Parameter(
			description = "읽음 처리할 알림의 고유 ID",
			required = true,
			example = "101"
		)
		@PathVariable Long notificationId
	);
}
