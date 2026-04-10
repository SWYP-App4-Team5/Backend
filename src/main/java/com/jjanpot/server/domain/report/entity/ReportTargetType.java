package com.jjanpot.server.domain.report.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportTargetType {
	USER("사용자 신고"),
	CERTIFICATION("게시글 신고");

	private final String description;
}
