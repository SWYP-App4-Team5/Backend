package com.jjanpot.server.domain.notification_template.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.jjanpot.server.global.util.CodeEnum;
import com.jjanpot.server.global.util.EnumUtils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationSubTemplateType implements CodeEnum {
	DAILY("ENCOURAGE", "DAILY", "매일"),
	START_OF_WEEK("ENCOURAGE", "START_OF_WEEK", "3일차"),
	MIDDLE_OF_WEEK("ENCOURAGE", "MIDDLE_OF_WEEK", "5일차"),
	END_OF_WEEK("ENCOURAGE", "END_OF_WEEK", "7일차"),
	;

	private final String groupCode;
	private final String code;
	private final String description;

	@JsonValue
	public String toJson() {
		return getCode();
	}

	@JsonCreator
	public static NotificationTemplateType from(String code) {
		return EnumUtils.fromCode(NotificationTemplateType.class, code);
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getDescription() {
		return description;
	}
}
