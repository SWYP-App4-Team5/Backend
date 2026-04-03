package com.jjanpot.server.domain.notification.service;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.jjanpot.server.domain.notification.entity.Notification;
import com.jjanpot.server.domain.notification.repository.NotificationRepository;
import com.jjanpot.server.global.service.push.FcmSendResult;

import lombok.RequiredArgsConstructor;

/**
 * 트랜잭션을 설정하기 위해
 * Notification 등록, 수정하는 Manager 객체 생성
 */
@Component
@RequiredArgsConstructor
public class NotificationManager {
	private final NotificationRepository notificationRepository;

	@Transactional
	public List<Notification> saveNotifications(List<Notification> notifications) {
		return notificationRepository.saveAll(notifications);
	}

	@Transactional
	public void updateResults(List<Notification> notifications, List<FcmSendResult> results) {
		for (int i = 0; i < notifications.size(); i++) {
			Notification notification = notifications.get(i);
			FcmSendResult sendResult = results.get(i);
			if (sendResult.isSuccess()) {
				notification.success(sendResult.messageId());
			} else {
				notification.fail(sendResult.errorCode(), sendResult.errorMessage());
			}
		}

		notificationRepository.saveAll(notifications);
	}

	@Transactional
	public void markAsFailed(List<Notification> notifications, String errorMsg) {
		for (Notification notification : notifications) {
			notification.fail("FCM_ERROR", errorMsg);
		}
		notificationRepository.saveAll(notifications);
	}
}
