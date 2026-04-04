package com.jjanpot.server.domain.notification.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jjanpot.server.domain.notification.dto.UserFcmDto;
import com.jjanpot.server.domain.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {
	// TODO 고도화 작업시 유저 설정에 알람 동의한 유저만 조회하게 WHERE 조건에 추가 필요.
	@Query("""
		SELECT DISTINCT new com.jjanpot.server.domain.notification.dto.UserFcmDto(u.userId, ud.fcmToken, cg.challengeId)
		FROM UserDevice ud
		JOIN ud.user u
		JOIN TeamMembers tm 
			ON tm.user = u
		JOIN tm.team t
		JOIN Challenge cg 
			ON cg.team = t AND cg.endDate > current_timestamp
		LEFT JOIN Certification cert ON cert.user = u\s
			AND cert.createdAt BETWEEN :startOfToday AND :endOfToday
		WHERE cert.certificationId IS NULL
	""")
	List<UserFcmDto> findTodayDidNotCertifyUser(
		@Param("startOfToday") LocalDateTime startOfToday,
		@Param("endOfToday") LocalDateTime endOfToday
	);
}
