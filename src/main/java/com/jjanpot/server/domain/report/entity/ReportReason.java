package com.jjanpot.server.domain.report.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportReason {
    INAPPROPRIATE_BEHAVIOR("부적절한 행동을 하는 사용자"),
    SPAM_OR_ADVERTISEMENT("스팸 또는 광고성 활동을 하는 사용자"),
    FRAUD_OR_FALSE_INFORMATION("사기 또는 허위 정보를 유포하는 사용자"),
    HARASSMENT_OR_DEFAMATION("괴롭힘 또는 비방을 하는 사용자"),
    PRIVACY_VIOLATION("개인정보를 침해하는 사용자"),
    ETC("기타 사유의 사용자");

    private final String description;
}
