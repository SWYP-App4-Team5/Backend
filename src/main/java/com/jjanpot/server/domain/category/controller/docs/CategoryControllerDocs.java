package com.jjanpot.server.domain.category.controller.docs;

import java.util.List;

import com.jjanpot.server.domain.category.dto.CategoryResponse;
import com.jjanpot.server.global.common.dto.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Category", description = "카테고리 API")
@SecurityRequirement(name = "bearerAuth")
public interface CategoryControllerDocs {

	@Operation(
		summary = "카테고리(절약 항목) 목록 조회",
		description = """
			챌린지 생성 시 선택 가능한 카테고리 목록과 각 카테고리별 금액 선택지를 조회합니다.
			- 절약 항목(7개) : 카페/디저트, 배달/외식, 교통/자동차, 취미/여가, 술/유흥, 쇼핑, 기타
			- 카테고리(절약 항목)는 sortOrder 기준으로 정렬됩니다.
			- amountOptions는 해당 카테고리의 권장 금액 선택지 목록입니다. (오름차순)
			- 챌린지 생성 시 카테고리별 기준 금액은 amountOptions 중 하나를 선택합니다.
			"""
	)
	@ApiResponse(responseCode = "200", description = "카테고리 목록 조회 성공")
	SuccessResponse<List<CategoryResponse>> getCategories();
}
