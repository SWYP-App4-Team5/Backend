package com.jjanpot.server.domain.block.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateBlockRequest(
    @NotNull Long blockedUserId,
    @NotNull Long challengeId
) {
}
