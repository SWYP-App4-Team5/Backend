package com.jjanpot.server.global.auth.oauth;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "oauth")
public class OAuthProperties {

	private final Kakao kakao;
	private final Google google;

	@Getter
	@RequiredArgsConstructor
	public static class Kakao {
		private final String userInfourl;
	}

	@Getter
	@RequiredArgsConstructor
	public static class Google {
		private final String userInfoUrl;
	}
}
