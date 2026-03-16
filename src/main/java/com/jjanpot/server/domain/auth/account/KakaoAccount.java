package com.jjanpot.server.domain.auth.account;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoAccount {

	private Profile profile;
	private String email;

	@Getter
	@NoArgsConstructor
	public static class Profile {
		private String nickname;

		@JsonProperty("profile_image_url")
		private String profileImageUrl;
	}
}
