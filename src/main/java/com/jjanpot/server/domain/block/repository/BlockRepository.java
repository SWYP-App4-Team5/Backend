package com.jjanpot.server.domain.block.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jjanpot.server.domain.block.entity.Block;
import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.user.entity.User;

public interface BlockRepository extends JpaRepository<Block, Long> {

    boolean existsByBlockerAndBlockedAndChallenge(User blocker, User blocked, Challenge challenge);
}
