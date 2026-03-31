package com.jjanpot.server.domain.terms.entity;

import com.jjanpot.server.global.entity.BaseEntity;

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
@Table(name = "terms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Terms extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "terms_id")
	private Long termsId;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private TermsType type;

	@Column(name = "version", nullable = false, length = 20)
	private String version;

	@Column(name = "title", nullable = false, length = 200)
	private String title;

	@Column(name = "url", nullable = false, columnDefinition = "TEXT")
	private String url;
}