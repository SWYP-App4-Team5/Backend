package com.jjanpot.server.domain.auth.repository;

import static com.jjanpot.server.domain.auth.entity.QRefreshToken.*;

import java.time.LocalDateTime;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class RefreshTokenRepositoryImpl implements RefreshTokenRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public int deleteAllByExpiresAtBefore(LocalDateTime now) {
		long deletedCount = jpaQueryFactory
			.delete(refreshToken)
			.where(refreshToken.expiresAt.lt(now))
			.execute();
		return (int)deletedCount;
	}
}
