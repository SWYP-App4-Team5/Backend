package com.jjanpot.server.domain.notification.entity;

import com.jjanpot.server.domain.user.entity.User;
import com.jjanpot.server.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "notification_id")
	private Long notificationId;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private NotificationType type;

	@Column(name = "title", length = 100)
	private String title;

	@Column(name = "body", nullable = false, length = 200)
	private String body;

	@Column(name = "is_read", nullable = false)
	@Builder.Default
	private Boolean isRead = false;

	@Column(name = "is_sent", nullable = false)
	@Builder.Default
	private Boolean isSent = false;

	@Column(name = "related_id")
	private Long relatedId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public void markAsRead() {
		this.isRead = true;
	}

	public void markAsSent() {
		this.isSent = true;
	}
}