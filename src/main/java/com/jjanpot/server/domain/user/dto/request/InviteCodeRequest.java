package com.jjanpot.server.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record InviteCodeRequest(
	@NotBlank(message = "초대 코드 입력은 필수입니다.")
	String code
) {
}
