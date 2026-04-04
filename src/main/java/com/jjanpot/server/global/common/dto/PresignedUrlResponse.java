package com.jjanpot.server.global.common.dto;

import com.jjanpot.server.global.infra.storage.FileUploader.PresignedUrlResult;

public record PresignedUrlResponse(
	String uploadUrl,
	String imageUrl
) {

	public static PresignedUrlResponse from(PresignedUrlResult result) {
		return new PresignedUrlResponse(result.uploadUrl(), result.imageUrl());
	}
}
