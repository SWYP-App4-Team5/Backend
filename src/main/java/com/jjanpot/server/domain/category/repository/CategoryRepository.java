package com.jjanpot.server.domain.category.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jjanpot.server.domain.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	// 카테고리 목록을 정렬 순서(sortOrder)대로 조회
	List<Category> findAllByOrderBySortOrderAsc();
}

// sort_order 1 → 카페/디저트
// sort_order 2 → 배달/외식
// sort_order 3 → 교통/자동차
// ...
// 앱 화면에서 카테고리 버튼 순서가 항상 일정하게 표시되도록 보장