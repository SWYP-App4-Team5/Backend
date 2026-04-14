package com.jjanpot.server.domain.auth.dto;

import com.jjanpot.server.domain.user.entity.User;

public record UserCreateResult(User user, boolean isNewUser) {
}
