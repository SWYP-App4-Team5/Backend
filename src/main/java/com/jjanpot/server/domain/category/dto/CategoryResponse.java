package com.jjanpot.server.domain.category.dto;

import java.util.List;

import com.jjanpot.server.domain.category.entity.Category;
import com.jjanpot.server.domain.category.entity.CategoryAmountOption;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리 조회 응답")
public record CategoryResponse(

	@Schema(description = "카테고리 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	Long categoryId,

	@Schema(description = "카테고리 이름 (enum 상수명)", example = "CAFE_DESSERT", requiredMode = Schema.RequiredMode.REQUIRED)
	String name,

	@Schema(description = "카테고리 한글 표시명", example = "카페/디저트", requiredMode = Schema.RequiredMode.REQUIRED)
	String displayName,

	@Schema(
		description = "카테고리 아이콘 URL",
		example = "https://example.com/icon.png",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED,
		nullable = true
	)
	String iconUrl,

	@Schema(
		description = "카테고리별 소비 기준 금액 선택지 목록 - 챌린지 생성 시 amount 입력에 활용 (직접 입력도 가능)",
		example = "외식/배달 [10000, 15000, 20000, 30000]",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	List<Long> amountOptions
) {
	public static CategoryResponse from(Category category, List<CategoryAmountOption> options) {
		return new CategoryResponse(
			category.getCategoryId(),
			category.getName().name(),
			category.getName().getDisplayName(),
			category.getIconUrl(),
			options.stream()
				.map(CategoryAmountOption::getAmount)
				.toList()
		);
	}
}
