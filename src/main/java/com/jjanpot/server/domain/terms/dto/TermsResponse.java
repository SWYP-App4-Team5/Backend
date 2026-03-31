package com.jjanpot.server.domain.terms.dto;

import com.jjanpot.server.domain.terms.entity.Terms;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "약관 조회 응답")
public record TermsResponse(

	@Schema(description = "약관 ID", example = "1")
	Long termsId,

	@Schema(description = "약관 타입", example = "SERVICE_TERMS")
	String type,

	@Schema(description = "약관 버전", example = "1.0.0")
	String version,

	@Schema(description = "약관 제목", example = "서비스 이용약관")
	String title,

	@Schema(description = "약관 노션 URL", example = "https://notion.so/terms")
	String url
) {
	public static TermsResponse from(Terms terms) {
		return new TermsResponse(
			terms.getTermsId(),
			terms.getType().name(),
			terms.getVersion(),
			terms.getTitle(),
			terms.getUrl()
		);
	}
}
