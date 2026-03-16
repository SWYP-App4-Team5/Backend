package com.jjanpot.server.domain.auth.controller.docs;

import org.springframework.web.bind.annotation.PathVariable;

import com.jjanpot.server.domain.auth.dto.LoginRequest;
import com.jjanpot.server.domain.auth.dto.LoginResponse;
import com.jjanpot.server.global.common.dto.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth", description = "소셜 로그인 API")
public interface AuthControllerV1Docs {

	@Operation(
		summary = "소셜 로그인",
		description = """
			소셜 로그인 API 입니다.
			
			지원 플랫폼
			- kakao
			
			클라이언트에서 받은 accessToken을 전달하면
			서버에서 사용자 정보를 조회하고 로그인 처리 후 JWT 토큰을 발급합니다.
			"""
	)
	@ApiResponse(responseCode = "200", description = "로그인 성공")
	SuccessResponse<LoginResponse> login(

		@Parameter(
			description = "소셜 로그인 플랫폼 (kakao)",
			example = "kakao"
		)
		@PathVariable String provider,

		@Parameter(description = "소셜 accessToken")
		@RequestBody LoginRequest request
	);
}
