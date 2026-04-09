package com.jjanpot.server.domain.block.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjanpot.server.domain.block.dto.request.CreateBlockRequest;
import com.jjanpot.server.domain.block.entity.Block;
import com.jjanpot.server.domain.block.repository.BlockRepository;
import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.challenge.repository.ChallengeRepository;
import com.jjanpot.server.domain.team.repository.TeamMembersRepository;
import com.jjanpot.server.domain.user.entity.User;
import com.jjanpot.server.domain.user.repository.UserRepository;
import com.jjanpot.server.global.exception.BusinessException;
import com.jjanpot.server.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlockService {

    private final BlockRepository blockRepository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final TeamMembersRepository teamMembersRepository;

    @Transactional
    public void block(Long blockerId, CreateBlockRequest request) {
        // 자기 자신 차단 방지
        if (blockerId.equals(request.blockedUserId())) {
            throw new BusinessException(ErrorCode.SELF_REPORT_OR_BLOCK);
        }

        User blocker = userRepository.findById(blockerId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        User blocked = userRepository.findById(request.blockedUserId())
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Challenge challenge = challengeRepository.findById(request.challengeId())
            .orElseThrow(() -> new BusinessException(ErrorCode.CHALLENGE_NOT_FOUND));

        // 동일 챌린지 참여 여부 검증
        validateSameChallenge(blockerId, request.blockedUserId(), challenge.getChallengeId());

        // 중복 차단 방지
        if (blockRepository.existsByBlockerAndBlockedAndChallenge(blocker, blocked, challenge)) {
            throw new BusinessException(ErrorCode.ALREADY_BLOCKED);
        }

        blockRepository.save(Block.of(blocker, blocked, challenge));
    }

    private void validateSameChallenge(Long userId1, Long userId2, Long challengeId) {
        boolean user1Participates = teamMembersRepository.existsByUserIdAndChallengeId(userId1, challengeId);
        boolean user2Participates = teamMembersRepository.existsByUserIdAndChallengeId(userId2, challengeId);
        if (!user1Participates || !user2Participates) {
            throw new BusinessException(ErrorCode.NOT_SAME_CHALLENGE_PARTICIPANT);
        }
    }
}
