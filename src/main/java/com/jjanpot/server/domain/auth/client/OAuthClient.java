package com.jjanpot.server.domain.auth.client;

import com.jjanpot.server.domain.auth.dto.OAuthUser;

public interface OAuthClient {
	OAuthUser getUserInfo(String accessToken);
}
