package com.jjanpot.server.domain.certification.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jjanpot.server.domain.certification.entity.Certification;
import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.user.entity.User;

public interface CertificationRepository extends JpaRepository<Certification, Long> {

	/** 특정 챌린지에서 특정 유저의 인증 목록 조회 */
	// ChallengeScheduler에서 챌린지 종료 시 개인 결과 산출에 활용
	List<Certification> findAllByChallengeAndUser(Challenge challenge, User user);

	/** 하루 인증 횟수 조회 (일일 최대 3회 제한 체크용) */
	long countByUserAndChallengeAndSpentAtBetween(
		User user, Challenge challenge,
		LocalDateTime start, LocalDateTime end
	);

	/** 챌린지 전체 인증 피드 조회 (최신순) */
	List<Certification> findAllByChallengeOrderByCreatedAtDesc(Challenge challenge);

	/** 챌린지 내 팀원별 절약 금액 합산 조회 */
	@Query("SELECT c.user.userId, COALESCE(SUM(c.savedAmount), 0) "
		+ "FROM Certification c "
		+ "WHERE c.challenge = :challenge "
		+ "GROUP BY c.user.userId")
	List<Object[]> sumSavedAmountPerUserByChallenge(@Param("challenge") Challenge challenge);

	/** 챌린지 내 팀원별 인증 횟수 조회 */
	@Query("SELECT c.user.userId, COUNT(c) "
		+ "FROM Certification c "
		+ "WHERE c.challenge = :challenge "
		+ "GROUP BY c.user.userId")
	List<Object[]> countCertPerUserByChallenge(@Param("challenge") Challenge challenge);

	/** 챌린지 내 특정 유저의 인증 날짜 목록 조회 (중복 제거, 정렬) */
	@Query("SELECT DISTINCT CAST(c.spentAt AS date) "
		+ "FROM Certification c "
		+ "WHERE c.challenge = :challenge AND c.user = :user "
		+ "ORDER BY CAST(c.spentAt AS date)")
	List<java.time.LocalDate> findDistinctCertDatesByUser(
		@Param("challenge") Challenge challenge,
		@Param("user") User user
	);

	/** 챌린지 내 전체 인증 날짜별 유저 수 조회 (팀 연속활동 계산용) */
	@Query("SELECT CAST(c.spentAt AS date), COUNT(DISTINCT c.user.userId) "
		+ "FROM Certification c "
		+ "WHERE c.challenge = :challenge "
		+ "GROUP BY CAST(c.spentAt AS date) "
		+ "ORDER BY CAST(c.spentAt AS date)")
	List<Object[]> countDistinctUsersByDateForChallenge(@Param("challenge") Challenge challenge);
}
