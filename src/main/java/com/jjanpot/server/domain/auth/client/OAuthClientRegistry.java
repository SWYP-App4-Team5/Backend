package com.jjanpot.server.domain.auth.client;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.jjanpot.server.domain.user.entity.Provider;
import com.jjanpot.server.global.exception.BusinessException;
import com.jjanpot.server.global.exception.ErrorCode;

@Component
public class OAuthClientRegistry {

	private final Map<Provider, OAuthClient> clients;

	public OAuthClientRegistry(
		KakaoApiClient kakaoApiClient,
		GoogleApiClient googleApiClient,
		AppleApiClient appleApiClient

	) {
		this.clients = new EnumMap<>(Provider.class);
		clients.put(Provider.KAKAO, kakaoApiClient);
		clients.put(Provider.GOOGLE, googleApiClient);
		clients.put(Provider.APPLE, appleApiClient);

	}

	public OAuthClient getAuthClient(Provider provider) {
		OAuthClient client = clients.get(provider);
		if (client == null) {
			throw unsupportedProviderException(provider);
		}
		return client;
	}

	private BusinessException unsupportedProviderException(Provider provider) {
		return new BusinessException(
			ErrorCode.INVALID_INPUT,
			provider.name() + "해당 로그인 플랫폼은 아직 지원하지 않습니다"
		);
	}

}
