package com.jjanpot.server.global.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

	private final JwtProperties jwtProperties;

	public String generateToken(Long userId) {
		return createToken(userId, jwtProperties.getExpiration());
	}

	public String generateRefreshToken(Long userId) {
		return createToken(userId, jwtProperties.getRefreshExpiration());
	}

	private String createToken(Long userId, Long expiration) {
		Date nowDate = new Date();
		Date expiryDate = new Date(nowDate.getTime() + expiration);

		return Jwts.builder()
			.subject(String.valueOf(userId))
			.issuedAt(nowDate)
			.expiration(expiryDate)
			.signWith(getJwtSigningKey())
			.compact();
	}

	public Long getUserIdFromToken(String token) {
		return Long.valueOf(parseClaims(token).getSubject());
	}

	public boolean validateToken(String token) {
		try {
			parseClaims(token);
			return true;
		} catch (Exception e) {
			log.debug("Invalid JWT token: {}", e.getMessage());
			return false;
		}
	}

	private Claims parseClaims(String token) {
		return Jwts.parser()
			.verifyWith(getJwtSigningKey())
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

	private SecretKey getJwtSigningKey() {
		byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
