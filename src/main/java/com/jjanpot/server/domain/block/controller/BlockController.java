package com.jjanpot.server.domain.block.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.jjanpot.server.domain.block.controller.docs.BlockControllerDocs;
import com.jjanpot.server.domain.block.dto.request.CreateBlockRequest;
import com.jjanpot.server.domain.block.service.BlockService;
import com.jjanpot.server.global.annotation.CurrentUserId;
import com.jjanpot.server.global.common.dto.SuccessResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/blocks/v1")
@RequiredArgsConstructor
public class BlockController implements BlockControllerDocs {

    private final BlockService blockService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<Void> block(
        @CurrentUserId Long blockerId,
        @Valid @RequestBody CreateBlockRequest request
    ) {
        blockService.block(blockerId, request);
        return SuccessResponse.created(null, "차단이 완료되었습니다.");
    }
}
