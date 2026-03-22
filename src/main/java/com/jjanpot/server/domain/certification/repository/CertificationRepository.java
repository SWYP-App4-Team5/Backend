package com.jjanpot.server.domain.certification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jjanpot.server.domain.certification.entity.Certification;
import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.user.entity.User;

public interface CertificationRepository extends JpaRepository<Certification, Long> {

	/** 특정 챌린지에서 특정 유저의 인증 목록 조회 */
	// ChallengeScheduler에서 챌린지 종료 시 개인 결과 산출에 활용
	List<Certification> findAllByChallengeAndUser(Challenge challenge, User user);
}
