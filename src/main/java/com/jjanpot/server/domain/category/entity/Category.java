package com.jjanpot.server.domain.category.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "category_id")
	private Long categoryId;

	@Enumerated(EnumType.STRING)
	@Column(name = "name", nullable = false)
	private CategoryName name;

	@Column(name = "default_amount", nullable = false)
	private Long defaultAmount;

	@Column(name = "icon_url", columnDefinition = "TEXT")
	private String iconUrl;

	@Column(name = "sort_order", nullable = false)
	private Integer sortOrder;
}