package com.jjanpot.server.domain.terms.controller.docs;

import java.util.List;

import com.jjanpot.server.domain.terms.dto.TermsResponse;
import com.jjanpot.server.global.common.dto.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Terms", description = "약관 API")
public interface TermsControllerDocs {

	@Operation(summary = "약관 목록 조회", description = "서비스에 등록된 약관 목록을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "약관 목록 조회 성공")
	SuccessResponse<List<TermsResponse>> getTerms();
}
