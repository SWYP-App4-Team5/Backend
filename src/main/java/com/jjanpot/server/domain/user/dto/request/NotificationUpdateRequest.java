package com.jjanpot.server.domain.user.dto.request;

public record NotificationUpdateRequest(
	boolean dailyEnabled,
	boolean weeklyEnabled,
	boolean marketingConsent
) {
}
