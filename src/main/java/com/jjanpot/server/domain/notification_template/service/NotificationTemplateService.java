package com.jjanpot.server.domain.notification_template.service;

import com.jjanpot.server.domain.notification_template.dto.CreateNotificationTemplateRequest;

public interface NotificationTemplateService {
	Long create(Long userId, CreateNotificationTemplateRequest createCertificationRequest);
}
