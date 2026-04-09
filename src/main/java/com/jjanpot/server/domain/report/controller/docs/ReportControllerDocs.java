package com.jjanpot.server.domain.report.controller.docs;

import com.jjanpot.server.domain.report.dto.request.CreateReportRequest;
import com.jjanpot.server.global.common.dto.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Report", description = "신고 API")
@SecurityRequirement(name = "bearerAuth")
public interface ReportControllerDocs {

    @Operation(
        summary = "사용자 신고",
        description = """
            동일 챌린지 참여자를 신고합니다.

            - 자기 자신은 신고할 수 없습니다.
            - 동일 챌린지 참여자만 신고할 수 있습니다.
            - 신고 사유는 하나 이상 선택해야 합니다.
            - 동일 대상에 대한 중복 신고는 별도 레코드로 저장됩니다.
            """
    )
    @ApiResponse(responseCode = "201", description = "신고 완료")
    SuccessResponse<Void> report(
        @Parameter(hidden = true) Long reporterId,
        CreateReportRequest request
    );
}
