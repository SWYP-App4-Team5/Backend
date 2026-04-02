package com.jjanpot.server.domain.challenge.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.challenge.entity.ChallengeStatus;
import com.jjanpot.server.domain.team.entity.Team;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

	/** 특정 팀의 모든 챌린지 목록 조회 (상태 무관) */
	// 팀의 챌린지 이력 조회 등에 활용
	List<Challenge> findAllByTeam(Team team);

	/** 특정 상태의 챌린지 전체 조회 */
	// ChallengeScheduler에서 매일 자정
	// WAITING 챌린지를 찾아 ONGOING으로 전환할 때 활용
	List<Challenge> findAllByStatus(ChallengeStatus status);

	/** 특정 팀에서 특정 상태의 챌린지 1개 조회 */
	// joinTeam()에서 초대코드로 참여할 때,
	// 해당 팀에 WAITING 챌린지가 있는지 확인할 때 활용
	Optional<Challenge> findByTeamAndStatus(Team team, ChallengeStatus status);

	/** 특정 팀에서 상태 목록 중 하나에 해당하는 챌린지 1개 조회 (최신 생성순) */
	// getCurrentChallenge()에서 홈화면 로드 시,
	//  WAITING 또는 ONGOING 챌린지가 있는지 한 번에 확인할 때 활용
	@Query("SELECT c FROM Challenge c WHERE c.team = :team AND c.status IN :statuses ORDER BY c.challengeId DESC")
	Optional<Challenge> findFirstByTeamAndStatusIn(Team team, List<ChallengeStatus> statuses);

	/** 특정 유저가 활성(WAITING/ONGOING) 챌린지에 참여 중인지 확인 */
	@Query("SELECT COUNT(c) > 0 FROM Challenge c "
		+ "JOIN TeamMembers tm ON tm.team = c.team "
		+ "WHERE tm.user.userId = :userId AND c.status IN :statuses")
	boolean existsActiveByUserIdAndStatusIn(
		@Param("userId") Long userId,
		@Param("statuses") List<ChallengeStatus> statuses
	);

	/** 특정 상태이면서 종료일이 지난 챌린지 전체 조회 */
	// ChallengeScheduler에서 매일 자정
	// ONGOING 챌린지의 end_date가 지났으면 COMPLETED/FAILED로 전환할 때 활용
	List<Challenge> findAllByStatusAndEndDateBefore(ChallengeStatus status, LocalDateTime dateTime);
}
