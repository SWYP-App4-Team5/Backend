package com.jjanpot.server.domain.category.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjanpot.server.domain.category.controller.docs.CategoryControllerDocs;
import com.jjanpot.server.domain.category.dto.CategoryResponse;
import com.jjanpot.server.domain.category.service.CategoryService;
import com.jjanpot.server.global.common.dto.SuccessResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories/v1")
public class CategoryController implements CategoryControllerDocs {

	private final CategoryService categoryService;

	@GetMapping
	public SuccessResponse<List<CategoryResponse>> getCategories() {
		List<CategoryResponse> response = categoryService.getCategories();
		return SuccessResponse.ok(response);
	}
}
