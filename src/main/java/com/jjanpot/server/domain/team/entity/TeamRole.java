package com.jjanpot.server.domain.team.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TeamRole {
	LEADER("팀장"),
	MEMBER("팀원");

	private final String displayName;
}