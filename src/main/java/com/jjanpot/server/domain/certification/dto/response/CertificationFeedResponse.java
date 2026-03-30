package com.jjanpot.server.domain.certification.dto.response;

import java.time.LocalDateTime;

import com.jjanpot.server.domain.certification.entity.Certification;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인증 피드 응답")
public record CertificationFeedResponse(

	@Schema(description = "인증 ID", example = "1")
	Long certificationId,

	@Schema(description = "지출 유형 한국어 (지출 | 무지출)", example = "지출")
	String spendType,

	@Schema(description = "카테고리 한국어 이름", example = "카페/디저트")
	String categoryName,

	@Schema(description = "작성자 닉네임", example = "오므라이스최고")
	String userNickname,

	@Schema(
		description = "메모",
		example = "텀블러에 담아서 먹었는데 그럭저럭",
		nullable = true
	)
	String memo,

	@Schema(description = "절약 금액 (기준 금액 - 소비 금액, 음수 가능)", example = "3500")
	int savedAmount,

	@Schema(description = "인증 사진 URL (S3)", example = "https://jjanpot-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/certification/67fe91dc-966a-4131-9ffa-1278d9d9ead3.png", nullable = true)
	String imageUrl,

	@Schema(description = "인증 생성 일시", example = "2027-08-15T09:35:00")
	LocalDateTime createdAt,

	@Schema(description = "좋아요 수", example = "0")
	int likeCount
) {

	public static CertificationFeedResponse from(Certification cert, int likeCount) {
		return new CertificationFeedResponse(
			cert.getCertificationId(),
			cert.getSpendType().getDisplayName(),
			cert.getCategory().getName().getDisplayName(),
			cert.getUser().getNickname(),
			cert.getMemo().isEmpty() ? null : cert.getMemo(),
			cert.getSavedAmount(),
			cert.getImageUrl(),
			cert.getCreatedAt(),
			likeCount
		);
	}
}
