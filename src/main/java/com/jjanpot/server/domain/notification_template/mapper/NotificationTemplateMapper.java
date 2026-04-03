package com.jjanpot.server.domain.notification_template.mapper;

import org.springframework.util.StringUtils;

import com.jjanpot.server.domain.notification_template.dto.CreateNotificationTemplateRequest;
import com.jjanpot.server.domain.notification_template.entity.NotificationTemplate;
import com.jjanpot.server.global.exception.BusinessException;
import com.jjanpot.server.global.exception.ErrorCode;

public class NotificationTemplateMapper {
	public static NotificationTemplate toEntity(CreateNotificationTemplateRequest request) {
		validate(request);

		return NotificationTemplate.builder()
			.type(request.type())
			.title(request.title())
			.body(request.body())
			.build();
	}

	private static void validate(CreateNotificationTemplateRequest request) {
		if (request.type() == null) {
			throw new BusinessException(ErrorCode.INVALID_INPUT, "알람 유형이 필요합니다.");
		}
		if (!StringUtils.hasText(request.title()) || request.title().length() < 2) {
			throw new BusinessException(ErrorCode.INVALID_INPUT, "알람 유형이 필요합니다.");
		}
		if (request.body() == null || request.body().length() < 2) {
			throw new BusinessException(ErrorCode.INVALID_INPUT, "알람 유형이 필요합니다.");
		}
	}
}
