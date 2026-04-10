package com.jjanpot.server.domain.report.dto.request;

import com.jjanpot.server.domain.report.entity.ReportReason;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "인증 게시글 신고 요청")
public record CreateCertificationReportRequest(

	@Schema(description = "신고 대상 인증 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "인증 ID는 필수입니다.")
	Long certificationId,

	@Schema(description = "신고 사유 (단일 선택)", example = "SPAM_OR_ADVERTISEMENT", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "신고 사유는 필수입니다.")
	ReportReason reason
) {
}
