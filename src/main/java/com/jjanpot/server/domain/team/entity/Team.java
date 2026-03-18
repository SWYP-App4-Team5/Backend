package com.jjanpot.server.domain.team.entity;

import org.hibernate.annotations.Comment;

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
	@Comment("팀 명")
	private String teamName;

	@Column(name = "invite_code", nullable = false, length = 30)
	@Comment("팀 초대코드")
	private String inviteCode;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	@Comment("팀 유형 (친구/연인/가족/소모임/기타)")
	private TeamType type;

	@Column(name = "current_member_count", nullable = false)
	@Builder.Default
	@Comment("현재 참여 인원. 챌린지 생성자(팀장) 포함")
	private int currentMemberCount = 1;

	@Column(name = "max_member_count", nullable = false)
	@Comment("팀장이 설정한 최대 참여 인원")
	private int maxMemberCount;

	public static Team of(String teamName, String inviteCode, TeamType type, int maxMemberCount) {
		return Team.builder()
			.teamName(teamName)
			.inviteCode(inviteCode)
			.type(type)
			.maxMemberCount(maxMemberCount)
			.build();
	}

	// 팀원 합류 시
	public void increaseMemberCount() {
		this.currentMemberCount++;
	}

	// 팀원 탈퇴 시
	public void decreaseMemberCount() {
		this.currentMemberCount--;
	}
}