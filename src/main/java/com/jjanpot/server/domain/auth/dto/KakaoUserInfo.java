package com.jjanpot.server.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jjanpot.server.domain.auth.account.KakaoAccount;
import com.jjanpot.server.domain.user.entity.Provider;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserInfo implements OAuthUser {

	private Long id;

	@JsonProperty("kakao_account")
	private KakaoAccount kakaoAccount;

	@Override
	public Provider getProvider() {
		return Provider.KAKAO;
	}

	@Override
	public String getProviderId() {
		return String.valueOf(id);
	}

	@Override
	public String getNickname() {
		return kakaoAccount.getProfile().getNickname();
	}

	@Override
	public String getEmail() {
		return kakaoAccount.getEmail();
	}

	@Override
	public String getProfileImageUrl() {
		return kakaoAccount.getProfile().getProfileImageUrl();
	}
}
