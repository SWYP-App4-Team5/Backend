package com.jjanpot.server.domain.team.entity;

import com.jjanpot.server.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
	name = "team",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "UQ_invite_code",
			columnNames = {"invite_code"}
		)
	}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Team extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "team_id")
	private Long teamId;

	@Column(name = "team_name", nullable = false, length = 100)
	private String teamName;

	@Column(name = "invite_code", nullable = false, length = 30)
	private String inviteCode;

	@Column(name = "min_member_count", nullable = false)
	@Builder.Default
	private Integer minMemberCount = 2;

	@Column(name = "max_member_count", nullable = false)
	@Builder.Default
	private Integer maxMemberCount = 8;
}