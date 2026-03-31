package com.jjanpot.server.domain.user.dto.request;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "온보딩 프로필 생성 요청")
public record ProfileCreateRequest(

	@Schema(description = "닉네임", example = "절약왕", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "닉네임은 필수입니다.")
	@Size(max = 10, message = "닉네임은 10자 이하여야 합니다.")
	String nickname,

	@Schema(description = "생년월일", example = "2000-01-15", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "생년월일은 필수입니다.")
	LocalDate birthDate
) {
}
