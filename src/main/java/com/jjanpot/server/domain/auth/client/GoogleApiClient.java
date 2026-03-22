package com.jjanpot.server.domain.auth.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.jjanpot.server.domain.auth.dto.GoogleUserInfo;
import com.jjanpot.server.domain.auth.dto.OAuthUser;
import com.jjanpot.server.global.auth.oauth.OAuthProperties;
import com.jjanpot.server.global.exception.BusinessException;
import com.jjanpot.server.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class GoogleApiClient implements OAuthClient {

	private final RestTemplate restTemplate;
	private final OAuthProperties oAuthProperties;

	@Override
	public OAuthUser getUserInfo(String accessToken) {
		try {
			return getGoogleUserInfoByAccessToken(accessToken);
		} catch (Exception e) {
			log.error("구글 사용자 정보 조회 과정에서 오류 발생", e);
			throw new BusinessException(ErrorCode.GOOGLE_API_CALL_FAILED);
		}
	}

	private GoogleUserInfo getGoogleUserInfoByAccessToken(String accessToken) {
		String url = oAuthProperties.getGoogle().getUserInfoUrl();
		HttpHeaders headers = createAuthHeaders(accessToken);
		HttpEntity<Void> request = new HttpEntity<>(headers);

		ResponseEntity<GoogleUserInfo> response = restTemplate.exchange(
			url,
			HttpMethod.GET,
			request,
			GoogleUserInfo.class
		);

		GoogleUserInfo body = response.getBody();
		if (body == null) {
			throw new BusinessException(ErrorCode.GOOGLE_API_CALL_FAILED);
		}

		return body;
	}

	private HttpHeaders createAuthHeaders(String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);
		return headers;
	}
}
