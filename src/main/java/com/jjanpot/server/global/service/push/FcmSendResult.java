package com.jjanpot.server.global.service.push;

public record FcmSendResult(String messageId, boolean isSuccess, String errorCode, String errorMessage) {

	public static FcmSendResult success(String messageId) {
		return new FcmSendResult(messageId, true, null, null);
	}

	public static FcmSendResult fail(String errorCode, String errorMessage) {
		return new FcmSendResult(null, false, errorCode, errorMessage);
	}
}
