package com.jjanpot.server.domain.user.dto.response;

import java.time.LocalDate;

public record ProfileCreateResponse(
	String profileImageUrl,
	String nickname,
	LocalDate birthDate
) {
	public static ProfileCreateResponse of(String profileImageUrl, String nickname, LocalDate birthDate) {
		return new ProfileCreateResponse(profileImageUrl, nickname, birthDate);
	}
}
