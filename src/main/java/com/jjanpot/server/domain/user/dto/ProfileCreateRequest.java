package com.jjanpot.server.domain.user.dto;

import java.time.LocalDate;

public record ProfileCreateRequest(
	String profileImageUrl,
	String nickname,
	LocalDate birthDate
) {
}
