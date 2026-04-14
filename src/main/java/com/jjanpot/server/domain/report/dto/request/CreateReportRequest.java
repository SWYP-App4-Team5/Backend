package com.jjanpot.server.domain.report.dto.request;

import com.jjanpot.server.domain.report.entity.ReportReason;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "사용자 신고 요청")
public record CreateReportRequest(

	@Schema(description = "신고 대상 유저 ID", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "신고 대상 유저 ID는 필수입니다.")
	Long reportedUserId,

	@Schema(description = "챌린지 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "챌린지 ID는 필수입니다.")
	Long challengeId,

	@Schema(description = "신고 사유 (단일 선택)", example = "HARASSMENT_OR_DEFAMATION", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "신고 사유는 필수입니다.")
	ReportReason reason
) {
}
