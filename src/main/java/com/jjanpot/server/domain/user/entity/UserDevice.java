package com.jjanpot.server.domain.user.entity;

import com.jjanpot.server.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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
	@Column(name = "user_device_id", nullable = false)
	private Long userDeviceId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

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
}
