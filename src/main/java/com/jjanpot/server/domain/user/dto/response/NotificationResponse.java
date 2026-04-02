package com.jjanpot.server.domain.user.dto.response;

import com.jjanpot.server.domain.user.entity.User;
import com.jjanpot.server.domain.user.entity.UserAgreement;

public record NotificationResponse(
	boolean dailyEnabled,
	boolean weeklyEnabled,
	boolean marketingConsent
) {
	public static NotificationResponse of(User user, UserAgreement agreement) {
		return new NotificationResponse(
			user.isNotificationDailyEnabled(),
			user.isNotificationWeeklyEnabled(),
			agreement.isMarketingConsent()
		);
	}
}
