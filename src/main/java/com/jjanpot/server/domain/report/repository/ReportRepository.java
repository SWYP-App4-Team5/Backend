package com.jjanpot.server.domain.report.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jjanpot.server.domain.report.entity.Report;
import com.jjanpot.server.domain.user.entity.User;

public interface ReportRepository extends JpaRepository<Report, Long> {

	/** 유저가 신고한 또는 신고당한 기록 조회 (회원 탈퇴용) */
	List<Report> findAllByReporterOrReported(User reporter, User reported);
}
