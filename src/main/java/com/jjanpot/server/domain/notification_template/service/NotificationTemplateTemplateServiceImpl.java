package com.jjanpot.server.domain.notification_template.service;

import org.springframework.stereotype.Service;

import com.jjanpot.server.domain.notification_template.dto.CreateNotificationTemplateRequest;
import com.jjanpot.server.domain.notification_template.entity.NotificationTemplate;
import com.jjanpot.server.domain.notification_template.mapper.NotificationTemplateMapper;
import com.jjanpot.server.domain.notification_template.repository.NotificationTemplateRepository;
import com.jjanpot.server.domain.user.entity.User;
import com.jjanpot.server.domain.user.repository.UserRepository;
import com.jjanpot.server.global.exception.BusinessException;
import com.jjanpot.server.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NotificationTemplateTemplateServiceImpl implements NotificationTemplateService {
	private final NotificationTemplateRepository notificationTemplateRepository;
	private final UserRepository userRepository;

	@Override
	public Long create(Long userId, CreateNotificationTemplateRequest createCertificationRequest) {
		NotificationTemplate notificationTemplate = NotificationTemplateMapper.toEntity(createCertificationRequest);
		notificationTemplateRepository.save(notificationTemplate);

		return notificationTemplate.getTemplateId();
	}

	// TODO 사용자 권한 컬럼 추가시 수정 필요
	private void validUserAuthority(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
	}
}
