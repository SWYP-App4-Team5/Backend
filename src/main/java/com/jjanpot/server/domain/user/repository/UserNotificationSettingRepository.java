package com.jjanpot.server.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jjanpot.server.domain.user.entity.UserNotificationSetting;

public interface UserNotificationSettingRepository extends JpaRepository<UserNotificationSetting, Long> {
}
