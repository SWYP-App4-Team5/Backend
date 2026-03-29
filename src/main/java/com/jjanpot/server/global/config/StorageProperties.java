package com.jjanpot.server.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {

	private final String bucket;
	private final String baseUrl;
}
