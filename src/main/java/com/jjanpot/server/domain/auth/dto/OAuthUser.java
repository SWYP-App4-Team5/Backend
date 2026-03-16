package com.jjanpot.server.domain.auth.dto;

import com.jjanpot.server.domain.user.entity.Provider;

public interface OAuthUser {
	Provider getProvider();

	String getProviderId();

	String getNickname();

	String getEmail();

	default String getProfileImageUrl() {
		return null;
	}
}
