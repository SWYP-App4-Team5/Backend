package com.jjanpot.server.global.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Schema(description = "API 성공 응답")
public class SuccessResponse<T> {

	@Schema(description = "HTTP 상태 코드", example = "200")
	private final int status;

	@Schema(description = "응답 데이터")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private final T data;

	@Schema(description = "응답 메시지", example = "요청 성공")
	private final String message;

	@Builder(access = AccessLevel.PRIVATE)
	private SuccessResponse(int status, T data, String message) {
		this.status = status;
		this.data = data;
		this.message = message;
	}

	// 200 OK (GET 조회 → 200)
	public static <T> SuccessResponse<T> ok(T data) {
		return SuccessResponse.<T>builder()
				.status(HttpStatus.OK.value())
				.message("요청 성공")
				.data(data)
				.build();
	}

	public static <T> SuccessResponse<T> ok(T data, String message) {
		return SuccessResponse.<T>builder()
				.status(HttpStatus.OK.value())
				.message(message)
				.data(data)
				.build();
	}

	// 201 Created (POST 생성 → 201)
	public static <T> SuccessResponse<T> created(T data) {
		return SuccessResponse.<T>builder()
				.status(HttpStatus.CREATED.value())
				.message("생성 완료")
				.data(data)
				.build();
	}

	public static <T> SuccessResponse<T> created(T data, String message) {
		return SuccessResponse.<T>builder()
				.status(HttpStatus.CREATED.value())
				.message(message)
				.data(data)
				.build();
	}

	// 204 No Content (DELETE 삭제 → 204)
	public static SuccessResponse<Void> noContent() {
		return SuccessResponse.<Void>builder()
				.status(HttpStatus.NO_CONTENT.value())
				.message("처리 완료")
				.build();
	}
}
