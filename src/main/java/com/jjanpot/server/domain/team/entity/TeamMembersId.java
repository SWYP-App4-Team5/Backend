package com.jjanpot.server.domain.team.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TeamMembersId implements Serializable {

	@Column(name = "team_id")
	private Long teamId;

	@Column(name = "user_id")
	private Long userId;
}