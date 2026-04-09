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

            - 자기 자신은 차단할 수 없습니다.
            - 동일 챌린지 참여자만 차단할 수 있습니다.
            - 동일 대상을 같은 챌린지에서 중복 차단할 수 없습니다.
            - 차단 후 해당 사용자의 인증 게시글이 피드에서 제외됩니다.
            """
    )
    @ApiResponse(responseCode = "201", description = "차단 완료")
    SuccessResponse<Void> block(
        @Parameter(hidden = true) Long blockerId,
        CreateBlockRequest request
    );
}
