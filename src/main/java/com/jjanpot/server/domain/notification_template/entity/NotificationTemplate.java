package com.jjanpot.server.domain.notification_template.entity;

import com.jjanpot.server.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "notification_template",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "UQ_template_type",
			columnNames = {"type" , "sub_type"}
		)
	}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NotificationTemplate extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "template_id")
	private Long templateId;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private NotificationTemplateType type;

	@Enumerated(EnumType.STRING)
	@Column(name = "sub_type", length = 50)
	private NotificationSubTemplateType subType;

	@Column(name = "title", nullable = false, length = 100)
	private String title;

	@Column(name = "body", nullable = false, length = 200)
	private String body;

	// TODO 고도화에서 관리자가 관리할 수 있게 활성화 여부 컬럼 추가 필요

	public static NotificationTemplate create(NotificationTemplateType type, NotificationSubTemplateType subType, String title, String body) {
		return NotificationTemplate.builder()
			.type(type)
			.subType(subType)
			.title(title)
			.body(body)
			.build();
	}
}
