package com.jjanpot.server.domain.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjanpot.server.domain.user.controller.docs.NotificationControllerDocs;
import com.jjanpot.server.domain.user.dto.request.NotificationUpdateRequest;
import com.jjanpot.server.domain.user.dto.response.NotificationResponse;
import com.jjanpot.server.domain.user.service.UserService;
import com.jjanpot.server.global.annotation.CurrentUserId;
import com.jjanpot.server.global.common.dto.SuccessResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users/v1/notifications")
public class NotificationSettingController implements NotificationControllerDocs {

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
