package com.jjanpot.server.global.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.jjanpot.server.global.auth.oauth.OAuthProperties;
import com.jjanpot.server.global.security.jwt.JwtProperties;

@Configuration
@EnableConfigurationProperties({
	OAuthProperties.class,
	JwtProperties.class,
	CorsProperties.class,
	AuthProperties.class,
	StorageProperties.class
})
public class AppConfig {
}
