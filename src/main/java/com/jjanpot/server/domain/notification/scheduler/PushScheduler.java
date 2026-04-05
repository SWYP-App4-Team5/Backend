package com.jjanpot.server.domain.notification.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jjanpot.server.domain.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushScheduler {
	private final NotificationService notificationService;

	@Scheduled(cron = "0 0 18 * * ?")
	public void scheduleDailyPush() {
		log.info("Daily Scheduled Push Started");
		notificationService.sendDailyReminder();
	}

	@Scheduled(cron = "0  0 20 * * ?")
	public void scheduleWeeklyPush() {
		log.info("Weekly Scheduled Push Started");
		notificationService.sendWeeklyReminder();
	}
}
