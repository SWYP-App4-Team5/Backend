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

	// TODO TEST 용 알림 설정 테스트 완료시 매일 18시로 변경
	@Scheduled(cron = "0 */2 * * * ?")
	// @Scheduled(cron = "0 0 18 * * ?")
	public void schedulePush() {
		log.info("schedulePush Started");
		notificationService.sendDailyReminder();
	}
}
