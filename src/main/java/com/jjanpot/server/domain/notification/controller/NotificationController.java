package com.jjanpot.server.domain.notification.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjanpot.server.domain.notification.service.NotificationService;
import com.jjanpot.server.global.annotation.CurrentUserId;
import com.jjanpot.server.global.common.dto.SuccessResponse;

import lombok.RequiredArgsConstructor;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/notification")
@RestController
public class NotificationController implements NotificationFacade {
	private final NotificationService notificationService;

	// TODO Principal 사용
	@Override
	@PatchMapping("/v1/{notificationId}")
	public SuccessResponse<Long> read(@CurrentUserId Long userId, @PathVariable Long notificationId) {
		notificationService.markAsRead(userId, notificationId);

		return SuccessResponse.created(null);
	}

	@Override
	@GetMapping("/v99/test/daily-push")
	public void dailyPushNotification() {
		notificationService.sendDailyReminder();
	}

	@Override
	@GetMapping("/v99/test/weekly-push")
	public void weeklyPushNotification() {
		notificationService.sendWeeklyReminder();
	}
}
