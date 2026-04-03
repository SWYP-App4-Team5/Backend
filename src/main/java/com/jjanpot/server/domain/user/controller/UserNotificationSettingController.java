package com.jjanpot.server.domain.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjanpot.server.domain.user.controller.docs.UserNotificationSettingControllerDocs;
import com.jjanpot.server.domain.user.dto.request.NotificationUpdateRequest;
import com.jjanpot.server.domain.user.dto.response.NotificationResponse;
import com.jjanpot.server.domain.user.service.UserService;
import com.jjanpot.server.global.annotation.CurrentUserId;
import com.jjanpot.server.global.common.dto.SuccessResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users/v1/notifications")
<<<<<<<< HEAD:src/main/java/com/jjanpot/server/domain/user/controller/NotificationSettingController.java
public class NotificationSettingController implements NotificationControllerDocs {
========
public class UserNotificationSettingController implements UserNotificationSettingControllerDocs {
>>>>>>>> 7181fc9 ([Fix] 알림 설정 컨트롤러 이름 수정):src/main/java/com/jjanpot/server/domain/user/controller/UserNotificationSettingController.java

	private final UserService userService;

	@GetMapping
	public SuccessResponse<NotificationResponse> getNotification(@CurrentUserId Long userId) {
		return SuccessResponse.ok(userService.getNotification(userId));
	}

	@PatchMapping
	public SuccessResponse<Void> updateNotification(
		@RequestBody NotificationUpdateRequest request,
		@CurrentUserId Long userId
	) {
		userService.updateNotification(userId, request);
		return SuccessResponse.ok(null);
	}
}
