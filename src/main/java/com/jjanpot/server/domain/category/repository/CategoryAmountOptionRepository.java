package com.jjanpot.server.domain.category.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jjanpot.server.domain.category.entity.Category;
import com.jjanpot.server.domain.category.entity.CategoryAmountOption;

public interface CategoryAmountOptionRepository extends JpaRepository<CategoryAmountOption, Long> {

	// 카테고리 1개에 대한 옵션 조회
	List<CategoryAmountOption> findAllByCategoryOrderBySortOrderAsc(Category category);

	// 카테고리 여러 개에 대한 옵션 한 번에 조회
	List<CategoryAmountOption> findAllByCategoryInOrderBySortOrderAsc(List<Category> categories);
}
