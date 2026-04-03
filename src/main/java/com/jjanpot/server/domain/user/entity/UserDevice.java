package com.jjanpot.server.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.jjanpot.server.domain.notification_template.entity.NotificationTemplateType;
import com.jjanpot.server.global.entity.BaseEntity;
import com.jjanpot.server.global.util.CodeEnum;
import com.jjanpot.server.global.util.EnumUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
	name = "user_device",
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_user_device_fcm_token", columnNames = {"fcm_token"})
	},
	indexes = {
		@Index(name = "idx_user_device_user_id", columnList = "user_id"),
		@Index(name = "idx_user_device_device_uuid", columnList = "device_uuid")
	}
)
public class UserDevice extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "device_id")
	private Long deviceId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "device_type")
	private DeviceType deviceType;

	@Column(name = "device_uuid", nullable = false, length = 100)
	private String deviceUuid;

	@Column(name = "fcm_token", nullable = false, length = 300)
	private String fcmToken;

	@Builder.Default
	@Column(name = "is_active", nullable = false, columnDefinition = "TINYINT(1)")
	private boolean isActive = true;

	public static UserDevice create(User user, String deviceUuid, String fcmToken) {
		return UserDevice.builder()
			.user(user)
			.deviceUuid(deviceUuid)
			.fcmToken(fcmToken)
			.isActive(true)
			.build();
	}

	public void update(User user, String deviceUuid, String fcmToken) {
		this.user = user;
		this.deviceUuid = deviceUuid;
		this.fcmToken = fcmToken;
	}

	public void activate() {
		this.isActive = true;
	}

	public void deactivate() {
		this.isActive = false;
	}

	@Getter
	@RequiredArgsConstructor
	public enum DeviceType implements CodeEnum {
		IOS("IOS", "애플"),
		ANDROID("ANDROID", "안드로이드"),
		;

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
}
