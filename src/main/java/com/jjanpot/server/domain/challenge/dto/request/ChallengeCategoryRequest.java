package com.jjanpot.server.domain.challenge.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ChallengeCategoryRequest(

	@NotNull(message = "카테고리 ID는 필수입니다.")
	Long categoryId,

	// 인증 시 실제 소비금액과 비교하는 기준값 (절약 금액 = 이 값 - 실제 소비금액)
	// category_amount_option의 선택지 중 하나를 고르거나 직접 입력 가능
	@NotNull(message = "카테고리 기준 금액은 필수입니다.")
	@Min(value = 1000, message = "카테고리 기준 금액은 1,000원 이상이어야 합니다.")
	int amount
) {
}
