package com.jjanpot.server.global.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.jjanpot.server.global.security.jwt.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;


	@Bean
	@Order(1)
	public SecurityFilterChain filterChainSwagger(HttpSecurity http) throws Exception {
		http
			.securityMatcher(
				"/swagger-ui.html",
				"/swagger-ui/**",
				"/v3/api-docs",
				"/v3/api-docs/**",
				"/v3/api-docs.yaml"
			)
			.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
			.csrf(csrf -> csrf.disable())
			.formLogin(form -> form.disable())
			.httpBasic(httpBasic -> httpBasic.disable())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		return http.build();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain filterChainApi(HttpSecurity http) throws
		Exception {
		http
			.csrf(csrf -> csrf.disable())
			.formLogin(form -> form.disable())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.httpBasic(httpBasic -> httpBasic.disable())
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(
					"/api/auth/v1/login/**",
					"/api/auth/v1/refresh"
				).permitAll()
				.anyRequest().authenticated());
		return http.build();
	}
}
