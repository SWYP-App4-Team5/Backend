package com.jjanpot.server.global.auth.oauth;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "oauth")
public class OAuthProperties {

	private final Kakao kakao;

	@Getter
	@RequiredArgsConstructor
	public static class Kakao {
		private final String userInfourl;
	}
}
