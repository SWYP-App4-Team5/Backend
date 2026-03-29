package com.jjanpot.server.domain.user.dto.request;

import java.time.LocalDate;

public record ProfileCreateRequest(
	String profileImageUrl,
	String nickname,
	LocalDate birthDate
) {
}
