package com.jjanpot.server.domain.auth.dto;

import com.jjanpot.server.domain.user.entity.User;

public record LoginUserInfo(
	Long userId,
	String nickname
) {
	public static LoginUserInfo from(User user) {
		return new LoginUserInfo(
			user.getUserId(),
			user.getNickname()
		);
	}
}
