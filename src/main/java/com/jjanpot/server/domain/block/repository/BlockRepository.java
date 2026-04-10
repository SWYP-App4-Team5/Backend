package com.jjanpot.server.domain.block.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jjanpot.server.domain.block.entity.Block;
import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.user.entity.User;

public interface BlockRepository extends JpaRepository<Block, Long> {

    boolean existsByBlockerAndBlockedAndChallenge(User blocker, User blocked, Challenge challenge);

    /** 특정 유저가 특정 챌린지에서 차단한 유저 ID 목록 조회 */
    @Query("SELECT b.blocked.userId FROM Block b WHERE b.blocker.userId = :blockerId AND b.challenge = :challenge")
    Set<Long> findBlockedUserIdsByBlockerAndChallenge(@Param("blockerId") Long blockerId, @Param("challenge") Challenge challenge);
}
