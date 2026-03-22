package com.jjanpot.server.domain.certification.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SpendType {
	SPEND("지출"),
	NO_SPEND("무지출");

	private final String displayName;
}