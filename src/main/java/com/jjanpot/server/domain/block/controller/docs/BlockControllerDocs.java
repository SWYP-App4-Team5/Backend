package com.jjanpot.server.domain.block.controller.docs;

import com.jjanpot.server.domain.block.dto.request.CreateBlockRequest;
import com.jjanpot.server.global.common.dto.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Block", description = "차단 API")
@SecurityRequirement(name = "bearerAuth")
public interface BlockControllerDocs {

    @Operation(
        summary = "사용자 차단",
        description = """
            동일 챌린지 참여자를 차단합니다.

            **기본 규칙:**
            - 자기 자신은 차단할 수 없습니다.
            - 동일 챌린지 참여자만 차단할 수 있습니다.
            - 동일 대상을 같은 챌린지에서 중복 차단할 수 없습니다.

            **차단 후 동작:**
            - 인증 피드에서 차단 대상의 게시글이 숨김 처리됩니다.

            **2인 챌린지:**
            - 차단 즉시 챌린지가 강제 종료(CANCELLED)됩니다.
            - 결과 리포트는 제공되지 않습니다.

            **3인 이상 챌린지:**
            - 팀원 절약 현황(members API)에서 차단 대상의 닉네임이 '차단한 사용자', 프로필 이미지가 null로 표시됩니다.
            - isBlocked=true로 반환되며, 프론트에서 회색 처리에 활용합니다.
            - 팀 절약 총액, 목표 달성률은 그대로 유지됩니다.
            """
    )
    @ApiResponse(responseCode = "201", description = "차단 완료")
    SuccessResponse<Void> block(
        @Parameter(hidden = true) Long blockerId,
        CreateBlockRequest request
    );
}
