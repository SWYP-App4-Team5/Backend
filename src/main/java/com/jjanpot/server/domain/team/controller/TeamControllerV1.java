package com.jjanpot.server.domain.team.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjanpot.server.domain.team.controller.docs.TeamControllerV1Docs;
import com.jjanpot.server.domain.team.dto.JoinTeamRequest;
import com.jjanpot.server.domain.team.dto.JoinTeamResponse;
import com.jjanpot.server.domain.team.service.TeamService;
import com.jjanpot.server.domain.user.entity.User;
import com.jjanpot.server.global.common.dto.SuccessResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/teams/v1")
public class TeamControllerV1 implements TeamControllerV1Docs {

	private final TeamService teamService;

	@PostMapping("/join")
	public SuccessResponse<JoinTeamResponse> joinTeam(
		@AuthenticationPrincipal User user,
		@Valid @RequestBody JoinTeamRequest request
	) {
		JoinTeamResponse response = teamService.joinTeam(user, request);
		return SuccessResponse.created(response);
	}
}
