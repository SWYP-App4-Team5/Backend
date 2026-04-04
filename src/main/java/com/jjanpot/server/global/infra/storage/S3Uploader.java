package com.jjanpot.server.global.infra.storage;

import java.time.Duration;

import org.springframework.stereotype.Component;

import com.jjanpot.server.global.config.StorageProperties;
import com.jjanpot.server.global.exception.BusinessException;
import com.jjanpot.server.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Uploader implements FileUploader {

	private static final String IMAGE_KEY_SUFFIX = "images/";

	private static final Duration PRESIGNED_URL_EXPIRATION = Duration.ofMinutes(10);

	private final StorageProperties storageProperties;
	private final S3Client s3Client;
	private final S3Presigner s3Presigner;

	@Override
	public String uploadImage(String key, byte[] content, String contentType) {
		try {
			String fullKey = IMAGE_KEY_SUFFIX + key;
			PutObjectRequest request = generatePutObjectRequest(fullKey, contentType);
			s3Client.putObject(request, RequestBody.fromBytes(content));

			String normalizedBaseUrl = storageProperties.getBaseUrl().replaceAll("/+$", "");
			return normalizedBaseUrl + "/" + fullKey;
		} catch (S3Exception e) {
			log.error("S3 upload failed: {}", e.getMessage(), e);
			throw new BusinessException(ErrorCode.IMAGE_UPLOAD_FAILED);
		} catch (SdkException e) {
			log.error("SDK upload failed: {}", e.getMessage(), e);
			throw new BusinessException(ErrorCode.IMAGE_UPLOAD_FAILED);
		}
	}

	@Override
	public void deleteImage(String imageUrl) {
		try {
			String normalizedBaseUrl = storageProperties.getBaseUrl().replaceAll("/+$", "");
			String key = imageUrl.replace(normalizedBaseUrl + "/", "");
			s3Client.deleteObject(DeleteObjectRequest.builder()
				.bucket(storageProperties.getBucket())
				.key(key)
				.build());
		} catch (S3Exception e) {
			log.error("S3 delete failed: {}", e.getMessage(), e);
			throw new BusinessException(ErrorCode.IMAGE_DELETE_FAILED);
		} catch (SdkException e) {
			log.error("SDK delete failed: {}", e.getMessage(), e);
			throw new BusinessException(ErrorCode.IMAGE_DELETE_FAILED);
		}
	}

	@Override
	public PresignedUrlResult generatePresignedUrl(String key, String contentType) {
		try {
			String fullKey = IMAGE_KEY_SUFFIX + key;

			PutObjectRequest putRequest = generatePutObjectRequest(fullKey, contentType);
			PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
				.signatureDuration(PRESIGNED_URL_EXPIRATION)
				.putObjectRequest(putRequest)
				.build();

			String uploadUrl = s3Presigner.presignPutObject(presignRequest).url().toString();
			String normalizedBaseUrl = storageProperties.getBaseUrl().replaceAll("/+$", "");
			String imageUrl = normalizedBaseUrl + "/" + fullKey;

			return new PresignedUrlResult(uploadUrl, imageUrl);
		} catch (SdkException e) {
			log.error("Presigned URL 생성 실패: {}", e.getMessage(), e);
			throw new BusinessException(ErrorCode.IMAGE_UPLOAD_FAILED);
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
