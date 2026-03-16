package com.jjanpot.server.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jjanpot.server.domain.user.entity.Provider;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleUserInfo implements OAuthUser {

	@JsonProperty("sub")
	private String providerId;

	@JsonProperty("email")
	private String email;

	@JsonProperty("name")
	private String name;

	@JsonProperty("picture")
	private String profileImageUrl;

	@Override
	public Provider getProvider() {
		return Provider.GOOGLE;
	}

	@Override
	public String getProviderId() {
		return providerId;
	}

	@Override
	public String getNickname() {
		return name;
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
