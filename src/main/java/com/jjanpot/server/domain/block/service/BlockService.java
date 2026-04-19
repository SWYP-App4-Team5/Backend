package com.jjanpot.server.domain.block.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjanpot.server.domain.block.dto.request.CreateBlockRequest;
import com.jjanpot.server.domain.block.entity.Block;
import com.jjanpot.server.domain.block.repository.BlockRepository;
import com.jjanpot.server.domain.certification.entity.Certification;
import com.jjanpot.server.domain.certification.repository.CertificationRepository;
import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.challenge.entity.ChallengeStatus;
import com.jjanpot.server.domain.challenge.repository.ChallengeRepository;
import com.jjanpot.server.domain.team.entity.Team;
import com.jjanpot.server.domain.team.repository.TeamMembersRepository;
import com.jjanpot.server.domain.user.entity.User;
import com.jjanpot.server.domain.user.repository.UserRepository;
import com.jjanpot.server.global.exception.BusinessException;
import com.jjanpot.server.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlockService {

    private final BlockRepository blockRepository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final TeamMembersRepository teamMembersRepository;
    private final CertificationRepository certificationRepository;

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

        applyBlockAndHide(blocker, blocked, challenge);

        // 2인 챌린지인 경우 강제 종료
        Team team = challenge.getTeam();
        if (team.getCurrentMemberCount() == 2
            && (challenge.getStatus() == ChallengeStatus.WAITING || challenge.getStatus() == ChallengeStatus.ONGOING)) {
            challenge.updateStatus(ChallengeStatus.CANCELLED);
            log.info("[2인 챌린지 강제 종료] challengeId={}, blockerId={}, blockedId={}",
                challenge.getChallengeId(), blockerId, request.blockedUserId());
        }
    }

    /**
     * 차단(멱등) + 피차단자의 챌린지 내 전체 인증 비노출 + 개발자 알림 로그.
     * 기존 차단이 있어도 이후 추가된 인증을 놓치지 않도록 비노출 처리는 항상 수행. 신고 → 차단 플로우에서 공용으로 사용.
     */
    @Transactional
    public void applyBlockAndHide(User blocker, User blocked, Challenge challenge) {
        boolean alreadyBlocked = blockRepository.existsByBlockerAndBlockedAndChallenge(blocker, blocked, challenge);
        if (!alreadyBlocked) {
            blockRepository.save(Block.of(blocker, blocked, challenge));
        }

        List<Certification> certifications = certificationRepository.findAllByChallengeAndUser(challenge, blocked);
        certifications.forEach(Certification::hide);

        log.info("[사용자 차단] blockerId={}, blockedId={}, challengeId={}, alreadyBlocked={}, targetCertCount={}",
            blocker.getUserId(), blocked.getUserId(), challenge.getChallengeId(), alreadyBlocked, certifications.size());
    }

    private void validateSameChallenge(Long userId1, Long userId2, Long challengeId) {
        boolean user1Participates = teamMembersRepository.existsByUserIdAndChallengeId(userId1, challengeId);
        boolean user2Participates = teamMembersRepository.existsByUserIdAndChallengeId(userId2, challengeId);
        if (!user1Participates || !user2Participates) {
            throw new BusinessException(ErrorCode.NOT_SAME_CHALLENGE_PARTICIPANT);
        }
    }
}
