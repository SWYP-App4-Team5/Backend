package com.jjanpot.server.domain.auth.client;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjanpot.server.domain.auth.dto.AppleUserInfo;
import com.jjanpot.server.domain.auth.dto.OAuthUser;
import com.jjanpot.server.global.exception.BusinessException;
import com.jjanpot.server.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class AppleApiClient implements OAuthClient {

	private final ObjectMapper objectMapper;

	@Override
	public OAuthUser getUserInfo(String identityToken) {
		try {
			return parseIdentityToken(identityToken);
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			log.error("애플 identityToken 파싱 중 오류 발생", e);
			throw new BusinessException(ErrorCode.APPLE_API_CALL_FAILED);
		}
	}

	private AppleUserInfo parseIdentityToken(String identityToken) {
		String[] parts = splitToken(identityToken);
		String payload = decodePayload(parts[1]); //payload
		return parseJson(payload);
	}

	private String[] splitToken(String identityToken) {
		String[] parts = identityToken.split("\\.");
		if (parts.length != 3) {
			throw new BusinessException(ErrorCode.INVALID_JWT_STRUCTURE);
		}
		return parts;
	}

	private String decodePayload(String encodedPayload) {
		try {
			return new String(
				Base64.getUrlDecoder().decode(encodedPayload),
				StandardCharsets.UTF_8
			);
		} catch (IllegalArgumentException e) {
			throw new BusinessException(ErrorCode.TOKEN_DECODE_FAILED);
		}
	}

	private AppleUserInfo parseJson(String payload) {
		try {
			return objectMapper.readValue(payload, AppleUserInfo.class);
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.TOKEN_PARSE_FAILED);
		}
	}
}
