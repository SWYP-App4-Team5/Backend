package com.jjanpot.server.domain.team.entity;

import com.jjanpot.server.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "team_members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TeamMembers {

	@EmbeddedId
	private TeamMembersId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("teamId")
	@JoinColumn(name = "team_id", nullable = false)
	private Team team;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("userId")
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	@Comment("팀 내 역할(팀장/팀원")
	private TeamRole role;

	@Column(name = "joined_at", nullable = false)
	@Comment("합류 일시")
	private LocalDateTime joinedAt;

	@PrePersist
	public void prePersist() {
		this.joinedAt = java.time.LocalDateTime.now();
	}
}