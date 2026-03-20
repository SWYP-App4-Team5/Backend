package com.jjanpot.server.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {
	private final int refreshTokenExpiresDays;//최대수명
	private final int refreshTokenReIssueThresholdDays; //재발급
}
