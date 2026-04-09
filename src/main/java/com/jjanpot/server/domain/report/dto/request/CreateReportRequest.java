package com.jjanpot.server.domain.report.dto.request;

import java.util.List;

import com.jjanpot.server.domain.report.entity.ReportReason;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateReportRequest(
    @NotNull Long reportedUserId,
    @NotNull Long challengeId,
    @NotEmpty(message = "신고 사유를 하나 이상 선택해야 합니다.")
    List<@NotNull @Valid ReportReasonRequest> reasons
) {
}
