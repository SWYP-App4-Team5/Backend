package com.jjanpot.server.global.security.jwt;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jjanpot.server.domain.user.entity.User;
import com.jjanpot.server.domain.user.repository.UserRepository;
import com.jjanpot.server.global.principal.UserPrincipal;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";

	private final JwtTokenProvider jwtTokenProvider;
	private final UserRepository userRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			setAuthenticationFromToken(request);
		} catch (Exception e) {
			log.error("사용자 인증 정보를 SecurityContext에 설정할 수 없습니다.", e);
		}
		filterChain.doFilter(request, response);
	}

	private void setAuthenticationFromToken(HttpServletRequest request) {
		String token = extractTokenFromRequest(request);

		if (token == null || !jwtTokenProvider.validateToken(token)) {
			return;
		}

		User user = getUserFromToken(token);
		if (user == null) {
			return;
		}

		setAuthentication(user);
	}

	private User getUserFromToken(String token) {
		Long userId = jwtTokenProvider.getUserIdFromToken(token);
		return userRepository.findById(userId).orElse(null);
	}

	private String extractTokenFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.substring(BEARER_PREFIX.length());
		}
		return null;

	}

	private void setAuthentication(User user) {
		UserPrincipal principal = new UserPrincipal(user.getUserId());

		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}

