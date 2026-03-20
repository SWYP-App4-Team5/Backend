package com.jjanpot.server.domain.auth.scheduler;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.jjanpot.server.domain.auth.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class RefreshTokenCleanupScheduler {

	private final RefreshTokenRepository refreshTokenRepository;

	@Transactional
	@Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
	public void cleanUpExpiredRefreshTokens() {
		LocalDateTime now = LocalDateTime.now();
		int deletedCount = refreshTokenRepository.deleteAllByExpiresAtBefore(now);
		log.info("[Token Scheduler] 만료된 리프레시 토큰 {}개 삭제 완료 (실행 시각: {})", deletedCount, now);
	}
}
