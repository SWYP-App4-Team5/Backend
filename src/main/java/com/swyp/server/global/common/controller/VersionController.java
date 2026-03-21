package com.swyp.server.global.common.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;

/**
 *
 */
@Hidden
@RestController
public class VersionController {
	@Value("${app.version:local}")
	private String version;

	@GetMapping("/version")
	public String version() {
		return "jjanpot version: " + version;
	}
}
