package com.jjanpot.server.global.infra.storage;

import org.springframework.stereotype.Component;

import com.jjanpot.server.global.config.StorageProperties;
import com.jjanpot.server.global.exception.StorageException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Uploader implements FileUploader {

	private static final String IMAGE_KEY_SUFFIX = "images/";

	private final StorageProperties storageProperties;
	private final S3Client s3Client;

	public String uploadImage(String key, byte[] content, String contentType) {
		try {
			String fullKey = IMAGE_KEY_SUFFIX + key;
			PutObjectRequest request = generatePutObjectRequest(fullKey, contentType);
			s3Client.putObject(request, RequestBody.fromBytes(content));

			String normalizedBaseUrl = storageProperties.getBaseUrl().replaceAll("/+$", "");
			return normalizedBaseUrl + "/" + fullKey;
		} catch (S3Exception e) {
			log.error("S3 Exception: {}", e.getMessage() , e);
			throw new StorageException(e);
		} catch (SdkException e) {
			log.error("SdkException: {}", e.getMessage() , e);
			throw new StorageException(e);
		}
	}

	private PutObjectRequest generatePutObjectRequest(String key, String contentType) {
		return PutObjectRequest.builder()
			.bucket(storageProperties.getBucket())
			.key(key)
			.contentType(contentType)
			.build();
	}
}
