// package com.jjanpot.server.domain.auth.service;
//
// import static org.assertj.core.api.Assertions.*;
// import static org.mockito.BDDMockito.*;
//
// import java.time.LocalDateTime;
// import java.util.Optional;
//
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// import com.jjanpot.server.domain.auth.client.OAuthClient;
// import com.jjanpot.server.domain.auth.client.OAuthClientRegistry;
// import com.jjanpot.server.domain.auth.controller.TokenTestData;
// import com.jjanpot.server.domain.auth.dto.OAuthUser;
// import com.jjanpot.server.domain.auth.dto.response.LoginResponse;
// import com.jjanpot.server.domain.auth.entity.RefreshToken;
// import com.jjanpot.server.domain.auth.repository.RefreshTokenRepository;
// import com.jjanpot.server.domain.user.entity.Provider;
// import com.jjanpot.server.domain.user.entity.User;
// import com.jjanpot.server.domain.user.repository.UserRepository;
// import com.jjanpot.server.global.config.AuthProperties;
// import com.jjanpot.server.global.security.jwt.JwtTokenProvider;
//
// @ExtendWith(MockitoExtension.class)
// public class AuthServiceTest {
//
// 	@Mock
// 	private OAuthClientRegistry oAuthClientRegistry;
//
// 	@Mock
// 	private UserRepository userRepository;
//
// 	@Mock
// 	private OAuthClient oAuthClient;
//
// 	@Mock
// 	private JwtTokenProvider jwtTokenProvider;
//
// 	@Mock
// 	private RefreshTokenRepository refreshTokenRepository;
//
// 	@Mock
// 	private AuthProperties authProperties;
//
// 	@InjectMocks
// 	private AuthService authService;
//
// 	private OAuthUser createUser() {
// 		return new OAuthUser() {
// 			@Override
// 			public Provider getProvider() {
// 				return Provider.KAKAO;
// 			}
//
// 			@Override
// 			public String getProviderId() {
// 				return "jjanpot260322";
// 			}
//
// 			@Override
// 			public String getNickname() {
// 				return "짠팟";
// 			}
//
// 			@Override
// 			public String getEmail() {
// 				return "test@gmail.com";
// 			}
//
// 			;
// 		};
// 	}
//
// 	@Test
// 	@DisplayName("신규 사용자 로그인시 user 객체 생성 과 토큰 발급")
// 	void login_newUser_success() {
// 		//given
// 		OAuthUser oAuthUser = createUser();
// 		User newUser = User.create(
// 			oAuthUser.getProvider(),
// 			oAuthUser.getProviderId(),
// 			oAuthUser.getNickname(),
// 			oAuthUser.getEmail(),
// 			oAuthUser.getProfileImageUrl()
// 		);
// 		User testUser = spy(newUser);
//
// 		given(testUser.getUserId()).willReturn(Long.valueOf("1"));
// 		given(oAuthClientRegistry.getAuthClient(Provider.KAKAO)).willReturn(oAuthClient);
// 		given(oAuthClient.getUserInfo(TokenTestData.KAKAO_ACCESS_TOKEN)).willReturn(oAuthUser);
// 		given(userRepository.findByProviderAndProviderId(
// 			Provider.KAKAO,
// 			"jjanpot260322"
// 		)).willReturn(Optional.empty());
// 		given(userRepository.save(any(User.class))).willReturn(testUser);
// 		given(jwtTokenProvider.generateToken(testUser.getUserId())).willReturn(TokenTestData.ACCESS_TOKEN);
// 		given(jwtTokenProvider.generateRefreshToken(testUser.getUserId())).willReturn(TokenTestData.REFRESH_TOKEN);
// 		given(refreshTokenRepository.findByUser(any())).willReturn(Optional.empty());
//
// 		//when
// 		LoginResponse response = authService.login(
// 			Provider.KAKAO,
// 			TokenTestData.KAKAO_ACCESS_TOKEN
// 		);
//
// 		//then
// 		assertThat(response.getAccessToken()).isEqualTo(TokenTestData.ACCESS_TOKEN);
// 		assertThat(response.getRefreshToken()).isEqualTo(TokenTestData.REFRESH_TOKEN);
//
// 		verify(testUser).updateLastLoginAt();
// 	}
//
// 	@Test
// 	@DisplayName("Refresh Token이 없는 경우 새로 생성")
// 	void login_withoutRefreshToken_success() {
// 		// given
// 		OAuthUser oAuthUser = createUser();
// 		User newUser = User.create(
// 			oAuthUser.getProvider(),
// 			oAuthUser.getProviderId(),
// 			oAuthUser.getNickname(),
// 			oAuthUser.getEmail(),
// 			oAuthUser.getProfileImageUrl()
// 		);
// 		User testUser = spy(newUser);
//
// 		given(testUser.getUserId()).willReturn(1L);
// 		given(authProperties.getRefreshTokenExpiresDays()).willReturn(30);
//
// 		given(oAuthClientRegistry.getAuthClient(Provider.KAKAO)).willReturn(oAuthClient);
// 		given(oAuthClient.getUserInfo(TokenTestData.KAKAO_ACCESS_TOKEN)).willReturn(oAuthUser);
// 		given(userRepository.findByProviderAndProviderId(
// 			Provider.KAKAO,
// 			"jjanpot260322"
// 		)).willReturn(Optional.empty());
// 		given(userRepository.save(any(User.class))).willReturn(testUser);
// 		given(jwtTokenProvider.generateToken(1L)).willReturn(TokenTestData.ACCESS_TOKEN);
// 		given(jwtTokenProvider.generateRefreshToken(1L)).willReturn(TokenTestData.REFRESH_TOKEN);
// 		given(refreshTokenRepository.findByUser(any(User.class))).willReturn(Optional.empty());
//
// 		// when
// 		LoginResponse response = authService.login(
// 			Provider.KAKAO,
// 			TokenTestData.KAKAO_ACCESS_TOKEN
// 		);
//
// 		// then
// 		assertThat(response.getAccessToken()).isEqualTo(TokenTestData.ACCESS_TOKEN);
// 		assertThat(response.getRefreshToken()).isEqualTo(TokenTestData.REFRESH_TOKEN);
// 		verify(refreshTokenRepository).save(any(RefreshToken.class));
// 	}
//
// 	@Test
// 	@DisplayName("Refresh Token이 있는 경우 기존 토큰을 업데이트")
// 	void login_withRefreshToken_success() {
// 		// given
// 		OAuthUser oAuthUser = createUser();
// 		User newUser = User.create(
// 			oAuthUser.getProvider(),
// 			oAuthUser.getProviderId(),
// 			oAuthUser.getNickname(),
// 			oAuthUser.getEmail(),
// 			oAuthUser.getProfileImageUrl()
// 		);
// 		User testUser = spy(newUser);
//
// 		given(testUser.getUserId()).willReturn(1L);
// 		given(authProperties.getRefreshTokenExpiresDays()).willReturn(30);
//
// 		RefreshToken existingToken = spy(
// 			RefreshToken.createRefreshToken(
// 				testUser,
// 				TokenTestData.OLD_REFRESH_TOKEN,
// 				LocalDateTime.now().plusDays(20)
// 			)
// 		);
//
// 		given(oAuthClientRegistry.getAuthClient(Provider.KAKAO)).willReturn(oAuthClient);
// 		given(oAuthClient.getUserInfo(TokenTestData.KAKAO_ACCESS_TOKEN)).willReturn(oAuthUser);
// 		given(userRepository.findByProviderAndProviderId(
// 			Provider.KAKAO,
// 			"jjanpot260322"
// 		)).willReturn(Optional.of(testUser));
// 		given(jwtTokenProvider.generateToken(anyLong())).willReturn(TokenTestData.ACCESS_TOKEN);
// 		given(jwtTokenProvider.generateRefreshToken(anyLong())).willReturn(TokenTestData.NEW_REFRESH_TOKEN);
// 		given(refreshTokenRepository.findByUser(any(User.class))).willReturn(Optional.of(existingToken));
//
// 		// when
// 		LoginResponse response = authService.login(
// 			Provider.KAKAO,
// 			TokenTestData.KAKAO_ACCESS_TOKEN
// 		);
//
// 		// then
// 		assertThat(response.getAccessToken()).isEqualTo(TokenTestData.ACCESS_TOKEN);
// 		assertThat(response.getRefreshToken()).isEqualTo(TokenTestData.NEW_REFRESH_TOKEN);
// 		verify(existingToken).updateToken(eq(TokenTestData.NEW_REFRESH_TOKEN), any(LocalDateTime.class));
// 		verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
// 	}
//
// }
