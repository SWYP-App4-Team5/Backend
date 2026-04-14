package com.jjanpot.server.domain.report.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.jjanpot.server.domain.report.controller.docs.ReportControllerDocs;
import com.jjanpot.server.domain.report.dto.request.CreateCertificationReportRequest;
import com.jjanpot.server.domain.report.dto.request.CreateReportRequest;
import com.jjanpot.server.domain.report.service.ReportService;
import com.jjanpot.server.global.annotation.CurrentUserId;
import com.jjanpot.server.global.common.dto.SuccessResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports/v1")
@RequiredArgsConstructor
public class ReportController implements ReportControllerDocs {

    private final ReportService reportService;

    @PostMapping("/user")
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<Void> report(
        @CurrentUserId Long reporterId,
        @Valid @RequestBody CreateReportRequest request
    ) {
        reportService.report(reporterId, request);
        return SuccessResponse.created(null, "신고가 접수되었습니다.");
    }

    @PostMapping("/certification")
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<Void> reportCertification(
        @CurrentUserId Long reporterId,
        @Valid @RequestBody CreateCertificationReportRequest request
    ) {
        reportService.reportCertification(reporterId, request);
        return SuccessResponse.created(null, "신고가 접수되었습니다.");
    }
}
