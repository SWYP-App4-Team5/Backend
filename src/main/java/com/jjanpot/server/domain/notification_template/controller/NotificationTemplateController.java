package com.jjanpot.server.domain.notification_template.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjanpot.server.domain.notification_template.dto.CreateNotificationTemplateRequest;
import com.jjanpot.server.domain.notification_template.service.NotificationTemplateService;
import com.jjanpot.server.global.annotation.CurrentUserId;
import com.jjanpot.server.global.common.dto.SuccessResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/notification/template/v1")
@RestController
public class NotificationTemplateController {
	private final NotificationTemplateService notificationTemplateService;

	@PostMapping
	public SuccessResponse<Long> create(@CurrentUserId Long userId, @Valid @RequestBody CreateNotificationTemplateRequest createNotificationTemplateRequest) {
		Long notificationTemplateId = notificationTemplateService.create(userId, createNotificationTemplateRequest);

		return SuccessResponse.created(notificationTemplateId);
	}
}
