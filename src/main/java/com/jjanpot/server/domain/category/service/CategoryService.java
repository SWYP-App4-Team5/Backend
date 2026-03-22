package com.jjanpot.server.domain.category.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjanpot.server.domain.category.dto.CategoryResponse;
import com.jjanpot.server.domain.category.entity.Category;
import com.jjanpot.server.domain.category.entity.CategoryAmountOption;
import com.jjanpot.server.domain.category.repository.CategoryAmountOptionRepository;
import com.jjanpot.server.domain.category.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

	private final CategoryRepository categoryRepository;
	private final CategoryAmountOptionRepository categoryAmountOptionRepository;

	/** 카테고리 목록 + 금액 선택지 조회 **/
	public List<CategoryResponse> getCategories() {
		List<Category> categories = categoryRepository.findAllByOrderBySortOrderAsc();

		// 카테고리별 금액 옵션을 한 번의 쿼리로 조회
		List<CategoryAmountOption> allOptions =
			categoryAmountOptionRepository.findAllByCategoryInOrderBySortOrderAsc(categories);

		Map<Long, List<CategoryAmountOption>> optionsByCategory = allOptions.stream()
			.collect(Collectors.groupingBy(opt -> opt.getCategory().getCategoryId()));

		return categories.stream()
			.map(category -> CategoryResponse.from(
				category,
				optionsByCategory.getOrDefault(category.getCategoryId(), List.of())
			))
			.toList();
	}
}
