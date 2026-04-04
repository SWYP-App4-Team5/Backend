package com.jjanpot.server.domain.notification.repository;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class NotificationRepositoryCustomImpl implements NotificationRepositoryCustom {
	private final JPAQueryFactory jpaQueryFactory;

}
