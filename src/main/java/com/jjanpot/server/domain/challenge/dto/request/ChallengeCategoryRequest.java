package com.jjanpot.server.domain.challenge.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ChallengeCategoryRequest(

	@NotNull(message = "카테고리 ID는 필수입니다.")
	Long categoryId,

	@NotNull(message = "카테고리 기준 금액은 필수입니다.")
	@Min(value = 1000, message = "카테고리 기준 금액은 1,000원 이상이어야 합니다.")
	int amount
) {
}
