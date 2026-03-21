package com.swyp.server.global.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@RestController
public class HealthCheckController {
	@GetMapping("/health")
	public ResponseEntity<String> health() {
		return ResponseEntity.ok("Healthy!!!!");
	}
}
