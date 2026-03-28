package com.jjanpot.server.domain.user.dto;

public record InviteCodeResponse(
	String inviteCode,
	String teamName
) {
	public static InviteCodeResponse of(String inviteCode, String teamName) {
		return new InviteCodeResponse(inviteCode, teamName);
	}
}
