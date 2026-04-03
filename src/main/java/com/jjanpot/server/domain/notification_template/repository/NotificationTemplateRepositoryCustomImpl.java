package com.jjanpot.server.domain.notification_template.repository;

import org.springframework.stereotype.Repository;

import com.jjanpot.server.domain.notification.repository.NotificationRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class NotificationTemplateRepositoryCustomImpl implements NotificationRepositoryCustom {
	private final JPAQueryFactory jpaQueryFactory;

}
