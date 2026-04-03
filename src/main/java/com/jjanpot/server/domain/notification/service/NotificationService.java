package com.jjanpot.server.domain.notification.service;

public interface NotificationService {
	void sendDailyReminder();

	void markAsRead(Long userId, Long notificationId);
}
