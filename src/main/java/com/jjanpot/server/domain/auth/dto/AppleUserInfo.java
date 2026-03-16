package com.jjanpot.server.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jjanpot.server.domain.user.entity.Provider;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AppleUserInfo implements OAuthUser {

	private String sub;
	private String email;

	@JsonProperty("email_verified")
	private Boolean emailVerified;

	@Override
	public Provider getProvider() {
		return Provider.APPLE;
	}

	@Override
	public String getProviderId() {
		return sub;
	}

	@Override
	public String getNickname() {
		if (isValidEmail(email)) {
			return email.split("@")[0];
		}
		return "apple.user";
	}

	@Override
	public String getEmail() {
		return email;
	}

	//todo: 기본 이미지 처리
	@Override
	public String getProfileImageUrl() {
		return null;
	}

	private boolean isValidEmail(String email) {
		return email != null && email.contains("@");
	}

}
