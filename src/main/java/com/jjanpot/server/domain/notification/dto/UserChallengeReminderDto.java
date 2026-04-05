package com.jjanpot.server.domain.notification.dto;

public record UserChallengeReminderDto(
	Long userId,
	String fcmToken,
	Long challengeId,
	Long days
) {
}
