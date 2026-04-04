package com.jjanpot.server.global.util;

import java.util.Arrays;

import com.jjanpot.server.global.exception.BusinessException;
import com.jjanpot.server.global.exception.ErrorCode;

public class EnumUtils {

	public static <T extends Enum<T> & CodeEnum> T fromCode(Class<T> enumClass, String code) {
		return Arrays.stream(enumClass.getEnumConstants())
			.filter(e -> e != null && e.getCode().equals(code))
			.findFirst()
			.orElseThrow(() -> new BusinessException(ErrorCode.INVALID_ENUM_INPUT,
				ErrorCode.INVALID_ENUM_INPUT.getMessage() + " : " + code));
	}
}
