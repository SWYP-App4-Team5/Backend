package com.jjanpot.server.domain.certification.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import com.jjanpot.server.domain.category.entity.Category;
import com.jjanpot.server.domain.certification.dto.request.CreateCertificationRequest;
import com.jjanpot.server.domain.certification.dto.response.CertificationFeedResponse;
import com.jjanpot.server.domain.certification.dto.response.CreateCertificationResponse;
import com.jjanpot.server.domain.certification.entity.Certification;
import com.jjanpot.server.domain.certification.entity.SpendType;
import com.jjanpot.server.domain.certification.repository.CertificationLikeRepository;
import com.jjanpot.server.domain.certification.repository.CertificationRepository;
import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.challenge.entity.ChallengeCategory;
import com.jjanpot.server.domain.challenge.entity.ChallengeStatus;
import com.jjanpot.server.domain.challenge.entity.ChallengeWeek;
import com.jjanpot.server.domain.challenge.repository.ChallengeCategoryRepository;
import com.jjanpot.server.domain.challenge.repository.ChallengeRepository;
import com.jjanpot.server.domain.challenge.repository.ChallengeWeekRepository;
import com.jjanpot.server.domain.team.repository.TeamMembersRepository;
import com.jjanpot.server.domain.user.entity.User;
import com.jjanpot.server.domain.user.repository.UserRepository;
import com.jjanpot.server.global.exception.BusinessException;
import com.jjanpot.server.global.exception.ErrorCode;
import com.jjanpot.server.global.infra.storage.FileUploader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CertificationService {

	private static final int DAILY_CERT_LIMIT = 3;
	private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Seoul");
	private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

	private final UserRepository userRepository;
	private final ChallengeRepository challengeRepository;
	private final ChallengeCategoryRepository challengeCategoryRepository;
	private final ChallengeWeekRepository challengeWeekRepository;
	private final TeamMembersRepository teamMembersRepository;
	private final CertificationRepository certificationRepository;
	private final CertificationLikeRepository certificationLikeRepository;
	private final FileUploader fileUploader;

	/** 인증 생성 **/
	@Transactional
	public CreateCertificationResponse createCertification(Long userId, CreateCertificationRequest request,
		MultipartFile image) {
		User user = findUser(userId);
		Challenge challenge = findChallenge(request.challengeId());

		// 팀원 검증
		teamMembersRepository.findByTeamAndUser(challenge.getTeam(), user)
			.orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN));

		// 챌린지 진행 중 여부 검증
		if (challenge.getStatus() != ChallengeStatus.ONGOING) {
			throw new BusinessException(ErrorCode.CHALLENGE_NOT_ONGOING);
		}

		// 지출 일시 검증: 미래 일시 불가
		LocalDateTime now = LocalDateTime.now(BUSINESS_ZONE);
		if (request.spentAt().isAfter(now)) {
			throw new BusinessException(ErrorCode.CERTIFICATION_SPENT_AT_FUTURE);
		}

		// 지출 일시 검증: 챌린지 기간 범위 확인
		if (request.spentAt().isBefore(challenge.getStartDate())
			|| request.spentAt().isAfter(challenge.getEndDate())) {
			throw new BusinessException(ErrorCode.CERTIFICATION_SPENT_AT_OUT_OF_RANGE);
		}

		// 일일 인증 횟수 제한 검증 (spentAt 날짜 기준, 하루 최대 3회)
		LocalDate spentDate = request.spentAt().toLocalDate();
		LocalDateTime startOfDay = spentDate.atStartOfDay();
		LocalDateTime startOfNextDay = spentDate.plusDays(1).atStartOfDay();
		long todayCount = certificationRepository.countByUserAndChallengeAndSpentAtBetween(
			user, challenge, startOfDay, startOfNextDay);
		if (todayCount >= DAILY_CERT_LIMIT) {
			throw new BusinessException(ErrorCode.CERTIFICATION_DAILY_LIMIT_EXCEEDED);
		}

		// 챌린지에 설정된 카테고리 검증
		ChallengeCategory challengeCategory = challengeCategoryRepository
			.findByChallengeAndCategory_CategoryId(challenge, request.categoryId())
			.orElseThrow(() -> new BusinessException(ErrorCode.CERTIFICATION_CATEGORY_NOT_IN_CHALLENGE));

		// 지출 금액 및 절약 금액 계산
		int spendAmount = resolveSpendAmount(request);
		int savedAmount = challengeCategory.getAmount() - spendAmount;

		// 현재 주차 조회 (MVP: 1주 고정)
		ChallengeWeek currentWeek = challengeWeekRepository
			.findByChallengeAndWeekNumber(challenge, 1)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

		// 이미지 업로드 (트랜잭션 롤백 시 업로드된 이미지 정리)
		String imageUrl = uploadImage(image);
		if (imageUrl != null) {
			deleteImageOnRollback(imageUrl);
		}

		// 인증 저장
		Category category = challengeCategory.getCategory();
		Certification certification = Certification.create(
			challenge, user, category, currentWeek,
			request.spendType(),
			spendAmount,
			savedAmount,
			request.memo() != null ? request.memo() : "",
			imageUrl,
			request.spentAt()
		);
		certificationRepository.save(certification);

		// 주차 절약 금액 누적
		currentWeek.addSavedAmount(savedAmount);

		return CreateCertificationResponse.from(certification);
	}

	/** 챌린지 인증 피드 조회 (최신순) **/
	public List<CertificationFeedResponse> getCertificationFeed(Long userId, Long challengeId) {
		User user = findUser(userId);
		Challenge challenge = findChallenge(challengeId);

		// 팀원 검증
		teamMembersRepository.findByTeamAndUser(challenge.getTeam(), user)
			.orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN));

		return certificationRepository.findAllByChallengeOrderByCreatedAtDesc(challenge)
			.stream()
			.map(cert -> CertificationFeedResponse.from(
				cert,
				certificationLikeRepository.countByCertificationAndDeletedAtIsNull(cert)
			))
			.toList();
	}

	/** 인증 수정 **/
	@Transactional
	public CreateCertificationResponse updateCertification(Long userId, Long certificationId,
		CreateCertificationRequest request, MultipartFile image) {
		User user = findUser(userId);
		Certification certification = findCertification(certificationId);

		// 본인 인증 검증
		validateOwner(certification, user);

		Challenge challenge = certification.getChallenge();

		// 챌린지 진행 중 여부 검증
		if (challenge.getStatus() != ChallengeStatus.ONGOING) {
			throw new BusinessException(ErrorCode.CHALLENGE_NOT_ONGOING);
		}

		// 지출 일시 검증: 미래 일시 불가
		LocalDateTime now = LocalDateTime.now(BUSINESS_ZONE);
		if (request.spentAt().isAfter(now)) {
			throw new BusinessException(ErrorCode.CERTIFICATION_SPENT_AT_FUTURE);
		}

		// 지출 일시 검증: 챌린지 기간 범위 확인
		if (request.spentAt().isBefore(challenge.getStartDate())
			|| request.spentAt().isAfter(challenge.getEndDate())) {
			throw new BusinessException(ErrorCode.CERTIFICATION_SPENT_AT_OUT_OF_RANGE);
		}

		// 일일 인증 횟수 제한 검증 (spentAt 날짜가 변경된 경우, 자기 자신 제외)
		LocalDate spentDate = request.spentAt().toLocalDate();
		LocalDate originalDate = certification.getSpentAt().toLocalDate();
		if (!spentDate.equals(originalDate)) {
			LocalDateTime startOfDay = spentDate.atStartOfDay();
			LocalDateTime startOfNextDay = spentDate.plusDays(1).atStartOfDay();
			long dayCount = certificationRepository.countByUserAndChallengeAndSpentAtBetween(
				user, challenge, startOfDay, startOfNextDay);
			if (dayCount >= DAILY_CERT_LIMIT) {
				throw new BusinessException(ErrorCode.CERTIFICATION_DAILY_LIMIT_EXCEEDED);
			}
		}

		// 챌린지에 설정된 카테고리 검증
		ChallengeCategory challengeCategory = challengeCategoryRepository
			.findByChallengeAndCategory_CategoryId(challenge, request.categoryId())
			.orElseThrow(() -> new BusinessException(ErrorCode.CERTIFICATION_CATEGORY_NOT_IN_CHALLENGE));

		// 지출 금액 및 절약 금액 계산
		int spendAmount = resolveSpendAmount(request);
		int newSavedAmount = challengeCategory.getAmount() - spendAmount;

		// 주차 절약 금액 보정 (기존 절약 금액 차감 → 새 절약 금액 누적)
		ChallengeWeek currentWeek = certification.getChallengeWeek();
		currentWeek.subtractSavedAmount(certification.getSavedAmount());
		currentWeek.addSavedAmount(newSavedAmount);

		// 이미지 처리: 새 이미지가 있으면 업로드 후 기존 이미지는 커밋 후 삭제
		String imageUrl = certification.getImageUrl();
		if (image != null && !image.isEmpty()) {
			String oldImageUrl = imageUrl;
			imageUrl = uploadImage(image);
			deleteImageOnRollback(imageUrl);
			if (oldImageUrl != null) {
				deleteImageAfterCommit(oldImageUrl);
			}
		}

		// 인증 업데이트
		Category category = challengeCategory.getCategory();
		certification.update(
			category,
			request.spendType(),
			spendAmount,
			newSavedAmount,
			request.memo() != null ? request.memo() : "",
			imageUrl,
			request.spentAt()
		);

		return CreateCertificationResponse.from(certification);
	}

	/** 인증 삭제 **/
	@Transactional
	public void deleteCertification(Long userId, Long certificationId) {
		User user = findUser(userId);
		Certification certification = findCertification(certificationId);

		// 본인 인증 검증
		validateOwner(certification, user);

		Challenge challenge = certification.getChallenge();

		// 챌린지 진행 중 여부 검증
		if (challenge.getStatus() != ChallengeStatus.ONGOING) {
			throw new BusinessException(ErrorCode.CHALLENGE_NOT_ONGOING);
		}

		// 주차 절약 금액 차감
		ChallengeWeek currentWeek = certification.getChallengeWeek();
		currentWeek.subtractSavedAmount(certification.getSavedAmount());

		// S3 이미지는 커밋 후 삭제
		if (certification.getImageUrl() != null) {
			deleteImageAfterCommit(certification.getImageUrl());
		}

		// 좋아요 삭제 후 인증 삭제
		certificationLikeRepository.deleteByCertification(certification);
		certificationRepository.delete(certification);
	}

	/** 트랜잭션 커밋 후 S3 이미지 삭제 (삭제 실패해도 DB에 영향 없음) */
	private void deleteImageAfterCommit(String imageUrl) {
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				try {
					fileUploader.deleteImage(imageUrl);
				} catch (Exception e) {
					log.warn("커밋 후 S3 이미지 삭제 실패 (고아 파일 발생 가능): {}", imageUrl, e);
				}
			}
		});
	}

	/** 트랜잭션 롤백 시 업로드된 S3 이미지 정리 */
	private void deleteImageOnRollback(String imageUrl) {
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCompletion(int status) {
				if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
					try {
						fileUploader.deleteImage(imageUrl);
					} catch (Exception e) {
						log.warn("롤백 후 S3 이미지 정리 실패 (고아 파일 발생 가능): {}", imageUrl, e);
					}
				}
			}
		});
	}

	private String uploadImage(MultipartFile image) {
		if (image == null || image.isEmpty()) {
			return null;
		}
		String contentType = image.getContentType();
		if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
			throw new BusinessException(ErrorCode.IMAGE_INVALID_FORMAT);
		}
		try {
			String extension = contentType.substring(contentType.indexOf('/') + 1);
			String key = "certification/" + UUID.randomUUID() + "." + extension;
			return fileUploader.uploadImage(key, image.getBytes(), contentType);
		} catch (IOException e) {
			throw new BusinessException(ErrorCode.IMAGE_UPLOAD_FAILED);
		}
	}

	private int resolveSpendAmount(CreateCertificationRequest request) {
		if (request.spendType() == SpendType.NO_SPEND) {
			return 0;
		}
		if (request.spentAmount() == null) {
			throw new BusinessException(ErrorCode.INVALID_INPUT, "지출 인증 시 소비 금액은 필수입니다.");
		}
		return request.spentAmount();
	}

	private User findUser(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
	}

	private Challenge findChallenge(Long challengeId) {
		return challengeRepository.findById(challengeId)
			.orElseThrow(() -> new BusinessException(ErrorCode.CHALLENGE_NOT_FOUND));
	}

	private Certification findCertification(Long certificationId) {
		return certificationRepository.findById(certificationId)
			.orElseThrow(() -> new BusinessException(ErrorCode.CERTIFICATION_NOT_FOUND));
	}

	private void validateOwner(Certification certification, User user) {
		if (!certification.getUser().getUserId().equals(user.getUserId())) {
			throw new BusinessException(ErrorCode.CERTIFICATION_NOT_OWNER);
		}
	}
}
