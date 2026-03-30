package com.jjanpot.server.domain.certification.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjanpot.server.domain.certification.controller.docs.CertificationControllerDocs;
import com.jjanpot.server.domain.certification.dto.request.CreateCertificationRequest;
import com.jjanpot.server.domain.certification.dto.response.CertificationFeedResponse;
import com.jjanpot.server.domain.certification.dto.response.CreateCertificationResponse;
import com.jjanpot.server.domain.certification.service.CertificationService;
import com.jjanpot.server.global.annotation.CurrentUserId;
import com.jjanpot.server.global.common.dto.SuccessResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/certifications/v1")
public class CertificationController implements CertificationControllerDocs {

	private final CertificationService certificationService;

	// 인증 생성
	@PostMapping
	public SuccessResponse<CreateCertificationResponse> createCertification(
		@CurrentUserId Long userId,
		@Valid @RequestBody CreateCertificationRequest request
	) {
		return SuccessResponse.created(certificationService.createCertification(userId, request));
	}

	// 인증 수정
	@PutMapping("/{certificationId}")
	public SuccessResponse<CreateCertificationResponse> updateCertification(
		@CurrentUserId Long userId,
		@PathVariable Long certificationId,
		@Valid @RequestBody CreateCertificationRequest request
	) {
		return SuccessResponse.ok(certificationService.updateCertification(userId, certificationId, request));
	}

	// 인증 삭제
	@DeleteMapping("/{certificationId}")
	public SuccessResponse<Void> deleteCertification(
		@CurrentUserId Long userId,
		@PathVariable Long certificationId
	) {
		certificationService.deleteCertification(userId, certificationId);
		return SuccessResponse.noContent();
	}

	// 챌린지의 인증 목록 조회
	@GetMapping("/challenge/{challengeId}")
	public SuccessResponse<List<CertificationFeedResponse>> getCertificationFeed(
		@CurrentUserId Long userId,
		@PathVariable Long challengeId
	) {
		return SuccessResponse.ok(certificationService.getCertificationFeed(userId, challengeId));
	}
}
