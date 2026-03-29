package com.jjanpot.server.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Validated
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {
	@NotBlank
	private final String bucket;
	@NotBlank
	private final String baseUrl;
}
