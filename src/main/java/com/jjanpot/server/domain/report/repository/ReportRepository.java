package com.jjanpot.server.domain.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jjanpot.server.domain.report.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
