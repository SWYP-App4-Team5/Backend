package com.swyp.server.global.common.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;

/**
 * 배포된게
 * Blue인지  Green인지
 * Blue - 8081
 * Green - 8082
 */
@Hidden
@RestController
public class BlueGreenController {

	@Value("${app.env:local}")
	private String env;

	@GetMapping("/env")
	public String env() {
		return "Current Environment: " + env;
	}
}
