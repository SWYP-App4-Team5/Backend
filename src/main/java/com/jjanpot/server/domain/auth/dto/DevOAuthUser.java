package com.jjanpot.server.domain.auth.dto;

import com.jjanpot.server.domain.user.entity.Provider;

public record DevOAuthUser(
	Provider provider,
	String providerId,
	String nickname,
	String email,
	String profileImageUrl
) implements OAuthUser {
	@Override
	public Provider getProvider() {
		return provider;
	}

	@Override
	public String getProviderId() {
		return providerId;
	}

	@Override
	public String getNickname() {
		return nickname;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public String getProfileImageUrl() {
		return profileImageUrl;
	}
}
