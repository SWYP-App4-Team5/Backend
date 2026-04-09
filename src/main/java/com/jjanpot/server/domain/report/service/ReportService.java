package com.jjanpot.server.domain.report.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.challenge.repository.ChallengeRepository;
import com.jjanpot.server.domain.report.dto.request.CreateReportRequest;
import com.jjanpot.server.domain.report.entity.Report;
import com.jjanpot.server.domain.report.repository.ReportRepository;
import com.jjanpot.server.domain.team.repository.TeamMembersRepository;
import com.jjanpot.server.domain.user.entity.User;
import com.jjanpot.server.domain.user.repository.UserRepository;
import com.jjanpot.server.global.exception.BusinessException;
import com.jjanpot.server.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final TeamMembersRepository teamMembersRepository;

    @Transactional
    public void report(Long reporterId, CreateReportRequest request) {
        // 자기 자신 신고 방지
        if (reporterId.equals(request.reportedUserId())) {
            throw new BusinessException(ErrorCode.SELF_REPORT_OR_BLOCK);
        }

        User reporter = userRepository.findById(reporterId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        User reported = userRepository.findById(request.reportedUserId())
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Challenge challenge = challengeRepository.findById(request.challengeId())
            .orElseThrow(() -> new BusinessException(ErrorCode.CHALLENGE_NOT_FOUND));

        // 동일 챌린지 참여 여부 검증
        validateSameChallenge(reporterId, request.reportedUserId(), challenge.getChallengeId());

        Report report = Report.of(reporter, reported, challenge);
        request.reasons().forEach(r -> report.addReason(r.reason()));
        reportRepository.save(report);
    }

    private void validateSameChallenge(Long userId1, Long userId2, Long challengeId) {
        boolean user1Participates = teamMembersRepository.existsByUserIdAndChallengeId(userId1, challengeId);
        boolean user2Participates = teamMembersRepository.existsByUserIdAndChallengeId(userId2, challengeId);
        if (!user1Participates || !user2Participates) {
            throw new BusinessException(ErrorCode.NOT_SAME_CHALLENGE_PARTICIPANT);
        }
    }
}
