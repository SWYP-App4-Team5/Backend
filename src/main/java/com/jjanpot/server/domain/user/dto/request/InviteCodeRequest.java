package com.jjanpot.server.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InviteCodeRequest(

	@Schema(
		description = "팀 초대코드 (6자리, 혼동 문자 O/0/I/1/L 제외)",
		example = "AB3K7Z",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	@NotBlank(message = "초대코드 입력은 필수입니다.")
	@Size(min = 6, max = 6, message = "초대코드는 6자리여야 합니다.")
	String inviteCode
) {
}
