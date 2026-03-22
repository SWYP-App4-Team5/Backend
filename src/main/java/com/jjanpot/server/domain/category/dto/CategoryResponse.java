package com.jjanpot.server.domain.category.dto;

import java.util.List;

import com.jjanpot.server.domain.category.entity.Category;
import com.jjanpot.server.domain.category.entity.CategoryAmountOption;

public record CategoryResponse(
	Long categoryId,
	String name,
	String iconUrl,
	List<Long> amountOptions
) {
	public static CategoryResponse from(Category category, List<CategoryAmountOption> options) {
		return new CategoryResponse(
			category.getCategoryId(),
			category.getName().name(),
			category.getIconUrl(),
			options.stream()
				.map(CategoryAmountOption::getAmount)
				.toList()
		);
	}
}
