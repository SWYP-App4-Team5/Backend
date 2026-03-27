package com.jjanpot.server.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjanpot.server.domain.user.dto.ProfileCreateRequest;
import com.jjanpot.server.domain.user.dto.ProfileCreateResponse;
import com.jjanpot.server.domain.user.entity.User;
import com.jjanpot.server.domain.user.repository.UserRepository;
import com.jjanpot.server.global.exception.BusinessException;
import com.jjanpot.server.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {

	private final UserRepository userRepository;

	//프로필 생성
	@Transactional
	public ProfileCreateResponse onboardingCreateProfile(ProfileCreateRequest request, Long userId) {
		User user = userRepository.findById(userId).orElseThrow(
			() -> new BusinessException(ErrorCode.USER_NOT_FOUND)
		);
		user.updateOnboarding(
			request.profileImageUrl(),
			request.nickname(),
			request.birthDate()
		);
		return ProfileCreateResponse.of(
			request.profileImageUrl(),
			request.nickname(),
			request.birthDate()
		);
	}
}
