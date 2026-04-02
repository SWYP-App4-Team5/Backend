package com.jjanpot.server.domain.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jjanpot.server.domain.auth.entity.RefreshToken;
import com.jjanpot.server.domain.user.entity.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>, RefreshTokenRepositoryCustom {

	Optional<RefreshToken> findByUser(User user);

	Optional<RefreshToken> findByToken(String token);

	void deleteByUser(User user);
}
