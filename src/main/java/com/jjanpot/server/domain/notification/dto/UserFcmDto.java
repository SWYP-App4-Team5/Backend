package com.jjanpot.server.domain.notification.dto;

public record UserFcmDto(Long userId, String fcmToken, Long challengeId) {

}
