package com.jjanpot.server.domain.auth.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.jjanpot.server.common.MockMvcTestSupport;
import com.jjanpot.server.domain.auth.dto.LoginRequest;
import com.jjanpot.server.domain.auth.dto.LoginResponse;
import com.jjanpot.server.domain.auth.dto.LoginUserInfo;
import com.jjanpot.server.domain.auth.service.AuthService;
import com.jjanpot.server.domain.user.entity.Provider;

class AuthControllerTest extends MockMvcTestSupport {

	private final AuthService authService = mock(AuthService.class);

	@Override
	protected Object initController() {
		return new AuthController(authService);
	}

	@Test
	@DisplayName("카카오로 신규사용자 로그인")
	void login_kakao_newUser() throws Exception {
		//given
		LoginRequest loginRequest = new LoginRequest(TokenTestData.KAKAO_ACCESS_TOKEN);
		LoginUserInfo loginUserInfo = new LoginUserInfo(Long.valueOf("20260322"), "짠팟");
		LoginResponse response = LoginResponse.of(
			TokenTestData.ACCESS_TOKEN,
			TokenTestData.REFRESH_TOKEN,
			loginUserInfo,
			true
		);

		given(authService.login(any(Provider.class), anyString()))
			.willReturn(response);

		//when & then
		mockMvc.perform(post("/api/auth/v1/login/kakao")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.accessToken").exists())
			.andExpect(jsonPath("$.data.refreshToken").exists())
			.andExpect(jsonPath("$.data.user.userId").value("20260322"))
			.andExpect(jsonPath("$.data.user.nickname").value("짠팟"))
			.andExpect(jsonPath("$.data.newUser").value(true));
	}

	@Test
	@DisplayName("카카오 기존 사용자 로그인")
	void login_kako_newUser() throws Exception {
		//given
		LoginRequest loginRequest = new LoginRequest(TokenTestData.KAKAO_ACCESS_TOKEN);

		LoginUserInfo loginUserInfo = new LoginUserInfo(Long.valueOf("20260322"), "짠팟");
		LoginResponse response = LoginResponse.of(
			TokenTestData.ACCESS_TOKEN,
			TokenTestData.REFRESH_TOKEN,
			loginUserInfo,
			false
		);

		given(authService.login(any(Provider.class), anyString()))
			.willReturn(response);

		//when & then
		mockMvc.perform(post("/api/auth/v1/login/kakao")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.accessToken").exists())
			.andExpect(jsonPath("$.data.refreshToken").exists())
			.andExpect(jsonPath("$.data.user.userId").value("20260322"))
			.andExpect(jsonPath("$.data.user.nickname").value("짠팟"))
			.andExpect(jsonPath("$.data.newUser").value(false));
	}
}
