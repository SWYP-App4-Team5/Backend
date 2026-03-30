package com.jjanpot.server.domain.certification.controller.docs;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.jjanpot.server.domain.certification.dto.request.CreateCertificationRequest;
import com.jjanpot.server.domain.certification.dto.response.CertificationFeedResponse;
import com.jjanpot.server.domain.certification.dto.response.CreateCertificationResponse;
import com.jjanpot.server.global.common.dto.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Certification", description = "인증 API")
@SecurityRequirement(name = "bearerAuth")
public interface CertificationControllerDocs {

	@Operation(
		summary = "인증 생성",
		description = """
			절약 인증을 생성합니다. (multipart/form-data)

			- 진행 중(ONGOING) 상태의 챌린지에서만 인증 가능합니다.
			- 하루 최대 3회까지 인증할 수 있습니다. (spentAt 기준, Asia/Seoul)
			- 챌린지에 설정된 카테고리만 선택할 수 있습니다.
			- 지출(SPEND): 소비 금액 필수 입력, 절약 금액 = 기준 금액 - 소비 금액 (음수 가능)
			- 무지출(NO_SPEND): 소비 금액 0으로 처리, 절약 금액 = 기준 금액
			- 이미지: 선택 입력, 최대 10MB (JPEG, PNG, WEBP)
			"""
	)
	@ApiResponse(responseCode = "201", description = "인증 생성 성공")
	SuccessResponse<CreateCertificationResponse> createCertification(
		@Parameter(hidden = true) Long userId,
		CreateCertificationRequest request,
		@Parameter(description = "인증 이미지 (선택, 최대 10MB, JPEG/PNG/WEBP)") MultipartFile image
	);

	@Operation(
		summary = "인증 수정",
		description = """
			본인의 절약 인증을 수정합니다. (multipart/form-data)

			- 진행 중(ONGOING) 상태의 챌린지에서만 수정 가능합니다.
			- 본인의 인증만 수정할 수 있습니다.
			- 수정 시 주차 절약 금액이 자동으로 보정됩니다 (기존 절약 금액 차감 → 새 절약 금액 누적).
			- 새 이미지를 업로드하면 기존 이미지는 삭제됩니다.
			"""
	)
	@ApiResponse(responseCode = "200", description = "인증 수정 성공")
	SuccessResponse<CreateCertificationResponse> updateCertification(
		@Parameter(hidden = true) Long userId,
		@Parameter(description = "인증 ID") Long certificationId,
		CreateCertificationRequest request,
		@Parameter(description = "새 인증 이미지 (선택, 최대 10MB, JPEG/PNG/WEBP)") MultipartFile image
	);

	@Operation(
		summary = "인증 삭제",
		description = """
			본인의 절약 인증을 삭제합니다.

			- 진행 중(ONGOING) 상태의 챌린지에서만 삭제 가능합니다.
			- 본인의 인증만 삭제할 수 있습니다.
			- 삭제 시 주차 절약 금액에서 해당 인증의 절약 금액이 차감됩니다.
			- 연관된 좋아요와 S3 이미지도 함께 삭제됩니다.
			"""
	)
	@ApiResponse(responseCode = "204", description = "인증 삭제 성공")
	SuccessResponse<Void> deleteCertification(
		@Parameter(hidden = true) Long userId,
		@Parameter(description = "인증 ID") Long certificationId
	);

	@Operation(
		summary = "챌린지의 인증 목록 조회",
		description = """
			특정 챌린지의 인증 피드를 최신순으로 목록 조회합니다.

			- 해당 챌린지 팀원만 조회할 수 있습니다.
			- savedAmount가 양수(+)이면 절약 성공, 음수(-)이면 기준 초과 소비입니다.
			"""
	)
	@ApiResponse(responseCode = "200", description = "인증 피드 조회 성공")
	SuccessResponse<List<CertificationFeedResponse>> getCertificationFeed(
		@Parameter(hidden = true) Long userId,
		@Parameter(description = "챌린지 ID") Long challengeId
	);
}