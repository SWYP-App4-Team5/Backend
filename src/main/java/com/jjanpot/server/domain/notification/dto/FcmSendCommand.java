package com.jjanpot.server.domain.notification.dto;

import com.jjanpot.server.domain.notification.entity.Notification;
import com.jjanpot.server.domain.notification_template.entity.NotificationTemplateType;

public record FcmSendCommand (
	String targetToken,
	String title,
	String body,
	NotificationTemplateType type,
	Long challengeId
) {
	public static FcmSendCommand from(Notification notification) {
		return new FcmSendCommand(
			notification.getTargetToken(),
			notification.getTitle(),
			notification.getBody(),
			notification.getNotificationTemplate().getType(),
			notification.getRelatedId()
		);
	}
}
