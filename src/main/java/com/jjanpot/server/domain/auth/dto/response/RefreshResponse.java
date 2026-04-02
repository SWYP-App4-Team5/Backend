package com.jjanpot.server.domain.auth.dto.response;

public record RefreshResponse(
	Long userId,
	String accessToken,
	String refreshToken
) {
	public static RefreshResponse of(Long userId, String accessToken, String refreshToken) {
		return new RefreshResponse(userId, accessToken, refreshToken);
	}
}
