package com.jjanpot.server.domain.report.controller.docs;

import com.jjanpot.server.domain.report.dto.request.CreateCertificationReportRequest;
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
            - 신고 사유는 1개만 선택합니다.
            - 동일 대상에 대한 중복 신고는 별도 레코드로 저장됩니다.
            """
    )
    @ApiResponse(responseCode = "201", description = "사용자 신고 완료")
    SuccessResponse<Void> report(
        @Parameter(hidden = true) Long reporterId,
        CreateReportRequest request
    );

    @Operation(
        summary = "인증 게시글 신고",
        description = """
            인증 게시글을 신고합니다.

            - 본인의 게시글은 신고할 수 없습니다.
            - 동일 챌린지 참여자만 신고할 수 있습니다.
            - 신고 사유는 1개만 선택합니다.
            - 신고 즉시 해당 게시글은 비노출(숨김) 처리됩니다.
            - 운영자 검토 후 정상 복구 또는 영구 삭제가 결정됩니다.

            **신고 사유 (ReportReason):**
            - INAPPROPRIATE_BEHAVIOR: 부적절한 행동
            - SPAM_OR_ADVERTISEMENT: 스팸 또는 광고성 활동
            - FRAUD_OR_FALSE_INFORMATION: 사기 또는 허위 정보 유포
            - HARASSMENT_OR_DEFAMATION: 괴롭힘 또는 비방
            - PRIVACY_VIOLATION: 개인정보 침해
            - ETC: 기타 사유
            """
    )
    @ApiResponse(responseCode = "201", description = "게시글 신고 완료")
    SuccessResponse<Void> reportCertification(
        @Parameter(hidden = true) Long reporterId,
        CreateCertificationReportRequest request
    );
}
