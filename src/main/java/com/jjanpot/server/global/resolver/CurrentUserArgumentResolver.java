package com.jjanpot.server.global.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.jjanpot.server.global.annotation.CurrentUserId;
import com.jjanpot.server.global.exception.BusinessException;
import com.jjanpot.server.global.exception.ErrorCode;
import com.jjanpot.server.global.principal.UserPrincipal;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(CurrentUserId.class)
			&& parameter.getParameterType().equals(Long.class);
	}

	@Override
	public Long resolveArgument(
		MethodParameter parameter,
		ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest,
		WebDataBinderFactory binderFactory) throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
			throw new BusinessException(ErrorCode.FORBIDDEN);
		}
		return principal.getUserId();
	}
}
