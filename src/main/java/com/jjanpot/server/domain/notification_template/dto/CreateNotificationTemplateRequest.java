package com.jjanpot.server.domain.notification_template.dto;

import com.jjanpot.server.domain.notification_template.entity.NotificationTemplateType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateNotificationTemplateRequest(
	@NotNull NotificationTemplateType type,
	@NotBlank @Size(min = 2) String title,
	@NotBlank @Size(min = 2) String body
) {
}
