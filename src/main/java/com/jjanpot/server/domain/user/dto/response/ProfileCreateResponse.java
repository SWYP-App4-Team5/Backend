package com.jjanpot.server.domain.user.dto.response;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "온보딩 프로필 생성 응답")
public record ProfileCreateResponse(

	@Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg", nullable = true)
	String profileImageUrl,

	@Schema(description = "닉네임", example = "절약왕")
	String nickname,

	@Schema(description = "생년월일", example = "2000-01-15")
	LocalDate birthDate
) {
	public static ProfileCreateResponse of(String profileImageUrl, String nickname, LocalDate birthDate) {
		return new ProfileCreateResponse(profileImageUrl, nickname, birthDate);
	}
}
