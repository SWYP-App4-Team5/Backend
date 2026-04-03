package com.jjanpot.server.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	INVALID_ENUM_INPUT(HttpStatus.BAD_REQUEST, "요청된 enum 값이 올바르지 않습니다."),
	INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
	NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
	HTTP_REQUEST_CONTEXT_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "HTTP 요청 컨텍스트를 찾을 수 없습니다."),

	INVALID_JWT_STRUCTURE(HttpStatus.BAD_REQUEST, "JWT 구조가 올바르지 않습니다."),
	TOKEN_DECODE_FAILED(HttpStatus.BAD_REQUEST, "토큰 디코딩에 실패했습니다."),
	TOKEN_PARSE_FAILED(HttpStatus.BAD_REQUEST, "토큰 파싱에 실패했습니다."),

	KAKAO_API_CALL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 API 호출에 실패했습니다."),
	GOOGLE_API_CALL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "구글 API 호출에 실패했습니다."),
	APPLE_API_CALL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "애플 API 호출에 실패했습니다."),

	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"),

	INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
	EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 리프레시 토큰입니다."),
	// Template
	TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "푸시 알림 템플릿을 찾을 수 없습니다."),
	// Messaging
	NOTIFICATION_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인가되지 않은 알림입니다."),
	NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "알림을 찾을 수 없습니다."),
	MESSAGE_REQUEST_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "메세지 발송에 실패했습니다."),
	// User Agreement
	REQUIRED_AGREEMENT_MISSING(HttpStatus.BAD_REQUEST, "필수 약관에 동의해야 합니다."),
	ALREADY_AGREED_TERMS(HttpStatus.BAD_REQUEST, "이미 약관 동의를 완료한 사용자입니다 "),
	USER_AGREEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자 약관 동의 정보를 찾을 수 없습니다."),

	// Challenge
	CHALLENGE_NOT_FOUND(HttpStatus.NOT_FOUND, "챌린지를 찾을 수 없습니다."),
	CHALLENGE_NOT_JOINABLE(HttpStatus.BAD_REQUEST, "참여 가능한 챌린지가 없습니다. 이미 시작되었거나 종료된 챌린지입니다."),
	CHALLENGE_ALREADY_ACTIVE(HttpStatus.BAD_REQUEST, "이미 진행 중이거나 대기 중인 챌린지가 있습니다."),
	CHALLENGE_CANCEL_FORBIDDEN(HttpStatus.BAD_REQUEST, "대기 중인 챌린지만 취소할 수 있습니다."),
	CHALLENGE_LEADER_REQUIRED(HttpStatus.FORBIDDEN, "팀장만 수행할 수 있는 작업입니다."),
	CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."),
	CHALLENGE_MIN_GOAL_POLICY_NOT_FOUND(HttpStatus.NOT_FOUND, "최소 목표 금액 정책을 찾을 수 없습니다."),
	GOAL_AMOUNT_BELOW_MINIMUM(HttpStatus.BAD_REQUEST, "팀 전체 목표 금액이 최소 기준에 미달합니다."),
	INVALID_CHALLENGE_START_DATE(HttpStatus.BAD_REQUEST, "챌린지 시작일은 오늘보다 이전일 수 없습니다."),
	INVALID_SAVED_AMOUNT(HttpStatus.BAD_REQUEST, "절약 금액은 0원 이상이어야 합니다."),
	SAVED_AMOUNT_UNDERFLOW(HttpStatus.BAD_REQUEST, "누적 절약 금액보다 크게 차감할 수 없습니다."),

	// Team
	TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "팀을 찾을 수 없습니다."),
	TEAM_ALREADY_FULL(HttpStatus.BAD_REQUEST, "팀 정원이 초과되었습니다."),
	ALREADY_TEAM_MEMBER(HttpStatus.BAD_REQUEST, "이미 팀에 참여한 사용자입니다."),

	// Certification
	CHALLENGE_NOT_ONGOING(HttpStatus.BAD_REQUEST, "진행 중인 챌린지에서만 인증할 수 있습니다."),
	CERTIFICATION_DAILY_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "하루 최대 3회까지 인증할 수 있습니다."),
	CERTIFICATION_CATEGORY_NOT_IN_CHALLENGE(HttpStatus.BAD_REQUEST, "챌린지에 설정된 카테고리만 선택할 수 있습니다."),
	CERTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "인증을 찾을 수 없습니다."),
	CERTIFICATION_NOT_OWNER(HttpStatus.FORBIDDEN, "본인의 인증만 수정/삭제할 수 있습니다."),
	CERTIFICATION_SPENT_AT_FUTURE(HttpStatus.BAD_REQUEST, "지출 일시는 현재 시간보다 이후일 수 없습니다."),
	CERTIFICATION_SPENT_AT_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "지출 일시는 챌린지 기간 내여야 합니다."),

	// Image
	IMAGE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "이미지 크기가 제한을 초과했습니다. (최대 10MB)"),
	IMAGE_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 형식입니다. (JPEG, PNG, WEBP만 허용)"),
	IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다."),
	IMAGE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 삭제에 실패했습니다.")
	;

	private final HttpStatus status;
	private final String message;
}
