package com.jjanpot.server.domain.user.dto.response;

import java.time.LocalDate;

import com.jjanpot.server.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 프로필 조회 응답")
public record UserProfileResponse(

	@Schema(description = "사용자 고유 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	Long userId,

	@Schema(description = "사용자 닉네임", example = "짠팟유저", requiredMode = Schema.RequiredMode.REQUIRED)
	String nickname,

	@Schema(
		description = "프로필 이미지 URL. 설정하지 않은 경우 기본 이미지 URL이 반환됩니다.",
		example = "https://jjanpot-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/profile/abc.png",
		requiredMode = Schema.RequiredMode.REQUIRED,
		nullable = true
	)
	String profileUrl,

	@Schema(description = "생년월일", example = "2000-01-15", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
	LocalDate birthDate
) {
	public static UserProfileResponse from(User user) {
		return new UserProfileResponse(
			user.getUserId(),
			user.getNickname(),
			user.getProfileImageUrl(),
			user.getBirthDate()
		);
	}
}
