package com.jjanpot.server.global.infra.storage;

public interface FileUploader {
	String uploadImage(String key, byte[] content, String contentType);

	void deleteImage(String imageUrl);

	PresignedUrlResult generatePresignedUrl(String key, String contentType);

	record PresignedUrlResult(String uploadUrl, String imageUrl) {
	}
}
