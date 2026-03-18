package com.jjanpot.server.domain.category.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jjanpot.server.domain.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	List<Category> findAllByOrderBySortOrderAsc();
}
