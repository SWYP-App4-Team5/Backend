package com.jjanpot.server.domain.notification.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.jjanpot.server.domain.notification.dto.FcmSendCommand;
import com.jjanpot.server.domain.notification.dto.UserFcmDto;
import com.jjanpot.server.domain.notification.entity.Notification;
import com.jjanpot.server.domain.notification.repository.NotificationRepository;
import com.jjanpot.server.domain.notification_template.entity.NotificationTemplate;
import com.jjanpot.server.domain.notification_template.entity.NotificationTemplateType;
import com.jjanpot.server.domain.notification_template.repository.NotificationTemplateRepository;
import com.jjanpot.server.domain.user.repository.UserRepository;
import com.jjanpot.server.global.exception.BusinessException;
import com.jjanpot.server.global.exception.ErrorCode;
import com.jjanpot.server.global.service.push.PushSendService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
	private final PushSendService pushSendService;
	private final NotificationTemplateRepository notificationTemplateRepository;
	private final NotificationRepository notificationRepository;
	private final NotificationManager notificationManager;
	private final UserRepository userRepository;

	@Value("${custom.fcm.partition-size:500}")
	private int partitionSize;

	@Override
	public void sendDailyReminder() {
		log.info("sendDailyReminder Started");
		List<UserFcmDto> userFcmList = getTodayDidNotCertifyUser();

		if (userFcmList.isEmpty()) {
			return;
		}

		// 실제 푸시알림 발송할 템플릿
		List<NotificationTemplate> notificationTemplate =
			notificationTemplateRepository.findTemplateByType(NotificationTemplateType.ENCOURAGE);

		if (CollectionUtils.isEmpty(notificationTemplate)) {
			throw new BusinessException(ErrorCode.TEMPLATE_NOT_FOUND);
		}

		Random random = new Random();

		Map<NotificationTemplate, List<UserFcmDto>> groupedTargets =
			userFcmList.stream()
				.collect(
					Collectors.groupingBy(
						userFcmDto -> notificationTemplate.get(random.nextInt(notificationTemplate.size()))));

		// 템플릿
		List<Notification> allNotificationList = initNotifications(userFcmList, notificationTemplate);

		// FCM은 500개가 넘어가면 성능상 좋지 않기 때문에 최대 500건 씩 발송
		List<List<Notification>> partitions = Lists.partition(allNotificationList, partitionSize);

		for (List<Notification> partition : partitions) {
			List<FcmSendCommand> fcmSendCommandList = partition.stream()
				.map(FcmSendCommand::from)
				.toList();

			pushSendService.sendMessage(fcmSendCommandList)
				.thenAccept(results -> {
					notificationManager.updateResults(partition, results);
				})
				.exceptionally(ex -> {
					log.error("FCM 배치 발송 중 오류 발생: {}", ex.getMessage());
					notificationManager.markAsFailed(partition, ex.getMessage());
					return null;
				});
		}
		log.info("schedulePush Ended");
	}

	@Override
	@Transactional
	public void markAsRead(Long userId, Long notificationId) {
		userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		Notification notification = notificationRepository.findById(notificationId)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));

		// 본인의 알림인지 권한 확인
		if (!notification.getUserId().equals(userId)) {
			throw new BusinessException(ErrorCode.NOTIFICATION_UNAUTHORIZED);
		}

		notification.markAsRead();

		notificationRepository.save(notification);
	}

	/**
	 * Notification 초기화
	 * @param targets 알림 발송 유저 fcm 정보
	 * @param templates 알림 템플릿
	 * @return 초기화된 알림 발송내역
	 */
	private List<Notification> initNotifications(List<UserFcmDto> targets, List<NotificationTemplate> templates) {
		Random random = ThreadLocalRandom.current();

		List<Notification> list = targets.stream()
			.map(target -> {
				if(!StringUtils.hasText(target.fcmToken())) {
					return null;
				}
				NotificationTemplate selectedTemplate = templates.get(random.nextInt(templates.size()));
				// PENDING 상태 생성
				return Notification.create(target.userId(), target.fcmToken(), selectedTemplate, target.challengeId());
			})
			.filter(Objects::nonNull)
			.toList();

		return notificationManager.saveNotifications(list);
	}

	/**
	 * TODO 사용자 설정에서 설정 true인 유저만 알림 보내게 설정해야 됨
	 * 오늘 인증하지 않은 사용자 정보를 조회
	 * @return
	 */
	private List<UserFcmDto> getTodayDidNotCertifyUser() {
		LocalDateTime start = LocalDate.now().atStartOfDay();
		LocalDateTime end = start.plusDays(1).minusNanos(1);

		return notificationRepository.findTodayDidNotCertifyUser(start, end);
	}

}
