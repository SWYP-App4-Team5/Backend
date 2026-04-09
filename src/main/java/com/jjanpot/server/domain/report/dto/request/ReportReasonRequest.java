package com.jjanpot.server.domain.report.dto.request;

import com.jjanpot.server.domain.report.entity.ReportReason;

import jakarta.validation.constraints.NotNull;

public record ReportReasonRequest(
    @NotNull ReportReason reason
) {
}
