package com.jjanpot.server.domain.notification.service;

public interface NotificationService {
	void sendDailyReminder();

	void sendWeeklyReminder();

	void markAsRead(Long userId, Long notificationId);
}
