package com.jjanpot.server.domain.report.entity;

import java.util.ArrayList;
import java.util.List;

import com.jjanpot.server.domain.certification.entity.Certification;
import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.user.entity.User;
import com.jjanpot.server.global.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "report")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private ReportTargetType targetType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_id")
    private User reported;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certification_id")
    private Certification certification;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportReasonEntity> reasons = new ArrayList<>();

    /** 사용자 신고 */
    public static Report ofUser(User reporter, User reported, Challenge challenge) {
        Report report = new Report();
        report.targetType = ReportTargetType.USER;
        report.reporter = reporter;
        report.reported = reported;
        report.challenge = challenge;
        return report;
    }

    /** 게시글(인증) 신고 */
    public static Report ofCertification(User reporter, Certification certification) {
        Report report = new Report();
        report.targetType = ReportTargetType.CERTIFICATION;
        report.reporter = reporter;
        report.certification = certification;
        report.reported = certification.getUser();
        report.challenge = certification.getChallenge();
        return report;
    }

    public void addReason(ReportReason reason) {
        this.reasons.add(ReportReasonEntity.of(this, reason));
    }
}
