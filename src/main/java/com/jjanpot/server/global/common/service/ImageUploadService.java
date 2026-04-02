package com.jjanpot.server.global.common.service;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import com.jjanpot.server.global.exception.BusinessException;
import com.jjanpot.server.global.exception.ErrorCode;
import com.jjanpot.server.global.infra.storage.FileUploader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class ImageUploadService {

	private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

	private final FileUploader fileUploader;

	//이미지 등록
	public String upload(MultipartFile file, String fileName) {
		String imgUrl = doUploadImage(file, fileName);
		if (imgUrl != null) {
			deleteImageOnRollback(imgUrl);
		}
		return imgUrl;
	}

	//이미지 삭제
	public void deleteImageAfterCommit(String imageUrl) {
		if (imageUrl == null) {
			return;
		}
		if (!TransactionSynchronizationManager.isSynchronizationActive()) {
			try {
				fileUploader.deleteImage(imageUrl);
			} catch (Exception e) {
				log.warn("트랜잭션 없이 즉시 S3 이미지 삭제 실패: {}", imageUrl, e);
			}
			return;
		}

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

	private String doUploadImage(MultipartFile image, String fileName) {
		if (image == null || image.isEmpty()) {
			return null;
		}
		String contentType = image.getContentType();
		if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
			throw new BusinessException(ErrorCode.IMAGE_INVALID_FORMAT);
		}
		try {
			String extension = contentType.substring(contentType.indexOf('/') + 1);
			String key = fileName + UUID.randomUUID() + "." + extension;
			return fileUploader.uploadImage(key, image.getBytes(), contentType);
		} catch (IOException e) {
			log.error("이미지 업로드 중 파일 읽기 실패", e);
			throw new BusinessException(ErrorCode.IMAGE_UPLOAD_FAILED);
		}
	}

	/** 트랜잭션 롤백 시 업로드된 S3 이미지 정리 */
	private void deleteImageOnRollback(String imageUrl) {
		if (!TransactionSynchronizationManager.isSynchronizationActive()) {
			return;
		}

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
}
