package com.jjanpot.server.global.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;

@Getter
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {

	private final List<String> allowedOrigins;

	public CorsProperties(List<String> allowedOrigins) {
		this.allowedOrigins = allowedOrigins;
	}
}
