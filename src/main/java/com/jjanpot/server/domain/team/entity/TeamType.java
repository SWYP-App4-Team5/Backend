package com.jjanpot.server.domain.team.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TeamType {
    FRIEND("친구"),
    COUPLE("연인"),
    FAMILY("가족"),
    CLUB("소모임"),
    OTHER("기타");

    private final String displayName;
}