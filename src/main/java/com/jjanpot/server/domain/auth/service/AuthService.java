package com.jjanpot.server.domain.auth.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjanpot.server.domain.auth.client.OAuthClient;
import com.jjanpot.server.domain.auth.client.OAuthClientRegistry;
import com.jjanpot.server.domain.auth.dto.LoginResponse;
import com.jjanpot.server.domain.auth.dto.LoginUserInfo;
import com.jjanpot.server.domain.auth.dto.OAuthUser;
import com.jjanpot.server.domain.auth.dto.RefreshResponse;
import com.jjanpot.server.domain.auth.entity.RefreshToken;
import com.jjanpot.server.domain.auth.repository.RefreshTokenRepository;
import com.jjanpot.server.domain.user.entity.Provider;
import com.jjanpot.server.domain.user.entity.User;
import com.jjanpot.server.domain.user.repository.UserRepository;
import com.jjanpot.server.global.config.AuthProperties;
import com.jjanpot.server.global.exception.BusinessException;
import com.jjanpot.server.global.exception.ErrorCode;
import com.jjanpot.server.global.security.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

	private final OAuthClientRegistry oAuthClientRegistry;
	private final UserRepository userRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;
	private final AuthProperties authProperties;

	@Transactional
	public LoginResponse login(Provider provider, String oauthAccessToken) {
		OAuthClient oauthClient = oAuthClientRegistry.getAuthClient(provider);
		OAuthUser oauthUserInfo = oauthClient.getUserInfo(oauthAccessToken);

		UserCreateResult userCreateResult = findOrCreateUser(provider, oauthUserInfo);
		User user = userCreateResult.user();

		String accessToken = jwtTokenProvider.generateToken(user.getUserId());
		String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());

		LocalDateTime expiresAt = LocalDateTime.now().plusDays(authProperties.getRefreshTokenExpiresDays());

		refreshTokenRepository.findByUser(user)
			.ifPresentOrElse(
				(token -> updateExistingToken(token, refreshToken, expiresAt)),
				() -> createNewToken(user, refreshToken, expiresAt)
			);
		LoginUserInfo userInfo = LoginUserInfo.from(user);
		return LoginResponse.of(accessToken, refreshToken, userInfo, userCreateResult.isNewUser());

	}

	@Transactional
	public RefreshResponse refreshToken(String token) {
		RefreshToken refreshToken = findRefreshToken(token);
		validateRefreshToken(refreshToken);

		User user = refreshToken.getUser();
		String newAccessToken = jwtTokenProvider.generateToken(user.getUserId());

		if (isRefreshTokenExpiringSoon(refreshToken)) {
			String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());
			updateRefreshToken(refreshToken, newRefreshToken);
			log.info("만료 임박 리프레시 토큰 갱신");

			return RefreshResponse.of(
				user.getUserId(),
				newAccessToken,
				newRefreshToken
			);

		}
		return RefreshResponse.of(
			user.getUserId(),
			newAccessToken,
			refreshToken.getToken()
		);
	}

	private UserCreateResult findOrCreateUser(Provider provider, OAuthUser oauthUser) {
		return userRepository
			.findByProviderAndProviderId(provider, oauthUser.getProviderId())
			.map(user -> new UserCreateResult(user, false))
			.orElseGet(() -> new UserCreateResult(createUser(provider, oauthUser), true));
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

	private void updateExistingToken(RefreshToken token, String tokenValue, LocalDateTime expiresAt) {
		token.updateToken(tokenValue, expiresAt);
	}

	private void createNewToken(User user, String token, LocalDateTime expiresAt) {
		RefreshToken refreshToken = RefreshToken.createRefreshToken(user, token, expiresAt);
		refreshTokenRepository.save(refreshToken);
	}

	private RefreshToken findRefreshToken(String token) {
		return refreshTokenRepository.findByToken(token)
			.orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));
	}

	private void validateRefreshToken(RefreshToken refreshToken) {
		if (refreshToken.isExpired()) {
			refreshTokenRepository.delete(refreshToken);
			log.info("만료된 리프레시 토큰 삭제: {}", refreshToken.getId());
			throw new BusinessException(ErrorCode.EXPIRED_REFRESH_TOKEN);
		}
	}

	private boolean isRefreshTokenExpiringSoon(RefreshToken refreshToken) {
		LocalDateTime threshold = LocalDateTime.now().plusDays(authProperties.getRefreshTokenReIssueThresholdDays());
		return refreshToken.getExpiresAt().isBefore(threshold);
	}

	private void updateRefreshToken(RefreshToken refreshToken, String newRefreshToken) {
		LocalDateTime newExpireTime = LocalDateTime.now().plusDays(authProperties.getRefreshTokenExpiresDays());
		refreshToken.updateToken(newRefreshToken, newExpireTime);
	}

	private record UserCreateResult(User user, boolean isNewUser) {
	}
}
