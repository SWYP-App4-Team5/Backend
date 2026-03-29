package com.jjanpot.server.global.principal;

public class UserPrincipal {

	private final Long userId;

	public UserPrincipal(Long userId) {
		this.userId = userId;
	}

	public Long getUserId() {
		return userId;
	}
}
