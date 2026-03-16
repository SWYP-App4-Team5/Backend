package com.jjanpot.server.domain.auth.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjanpot.server.domain.auth.client.OAuthClient;
import com.jjanpot.server.domain.auth.client.OAuthClientRegistry;
import com.jjanpot.server.domain.auth.dto.LoginResponse;
import com.jjanpot.server.domain.auth.dto.LoginUserInfo;
import com.jjanpot.server.domain.auth.dto.OAuthUser;
import com.jjanpot.server.domain.auth.entity.RefreshToken;
import com.jjanpot.server.domain.auth.repository.RefreshTokenRepository;
import com.jjanpot.server.domain.user.entity.Provider;
import com.jjanpot.server.domain.user.entity.User;
import com.jjanpot.server.domain.user.repository.UserRepository;
import com.jjanpot.server.global.security.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

	private static final int REFRESH_TOKEN_EXPIRES_DAYS = 30; //최대 수명
	private static final int REFRESH_TOKEN_REFRESH_DAYS = 2; //재발급

	private final OAuthClientRegistry oAuthClientRegistry;
	private final UserRepository userRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;

	@Transactional
	public LoginResponse login(Provider provider, String oauthAccessToken) {
		OAuthClient oauthClient = oAuthClientRegistry.getAuthClient(provider);
		OAuthUser oauthUserInfo = oauthClient.getUserInfo(oauthAccessToken);

		User user = findOrCreateUser(provider, oauthUserInfo);
		boolean isNewUser = isNewUser(user);

		String accessToken = jwtTokenProvider.generateToken(user.getUserId());
		String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());

		LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);

		refreshTokenRepository.findByUser(user)
			.ifPresentOrElse(
				(token -> updateExistingToken(token, refreshToken, expiresAt)),
				() -> createNewToken(user, refreshToken, expiresAt)
			);
		LoginUserInfo userInfo = LoginUserInfo.from(user);
		return LoginResponse.of(accessToken, refreshToken, userInfo, isNewUser);

	}

	private User findOrCreateUser(Provider provider, OAuthUser oauthUser) {
		return userRepository
			.findByProviderAndProviderId(provider, oauthUser.getProviderId())
			.orElseGet(() -> createUser(provider, oauthUser));
	}

	private User createUser(Provider provider, OAuthUser oauthUser) {
		User user = User.create(
			provider,
			oauthUser.getProviderId(),
			oauthUser.getNickname(),
			oauthUser.getEmail(),
			oauthUser.getProfileImageUrl()
		);
		return userRepository.save(user);
	}

	private boolean isNewUser(User user) {
		return user.getCreatedAt().isAfter(LocalDateTime.now().minusSeconds(5));
	}

	private void updateExistingToken(RefreshToken token, String tokenValue, LocalDateTime expiresAt) {
		token.updateToken(tokenValue, expiresAt);
	}

	private void createNewToken(User user, String token, LocalDateTime expiresAt) {
		RefreshToken refreshToken = RefreshToken.createRefreshToken(user, token, expiresAt);
		refreshTokenRepository.save(refreshToken);
	}
}
