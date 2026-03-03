package com.jjanpot.server.global.swagger;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import com.jjanpot.server.global.common.dto.ErrorResponse;
import com.jjanpot.server.global.exception.ErrorCode;
import com.jjanpot.server.global.swagger.annotation.ApiErrorCodeExample;
import com.jjanpot.server.global.swagger.annotation.ApiErrorCodeExamples;
import com.jjanpot.server.global.swagger.annotation.ExampleHolder;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		SecurityScheme securityScheme = new SecurityScheme()
			.type(SecurityScheme.Type.HTTP)
			.scheme("bearer")
			.bearerFormat("JWT")
			.description("Bearer는 자동으로 붙으므로, eyJhbGciOi... 와 같은 순수 토큰 문자열만 입력하시면 됩니다.");

		SecurityRequirement securityRequirement = new SecurityRequirement().addList("JWT TOKEN");

		return new OpenAPI()
			.components(new Components().addSecuritySchemes("JWT TOKEN", securityScheme))
			.addSecurityItem(securityRequirement)
			.info(new Info()
				.title("Jjanpot(짠팟) API 명세서")
				.version("1.0")
				.description("🔐토큰 인증이 필요한 API는 상단의 Authorize 버튼을 클릭한 뒤, `토큰`만 입력해주세요"))
			.servers(List.of(
				new Server().url("http://localhost:8080").description("Local server")
			));
	}

	@Bean
	public OperationCustomizer customize() {
		return (Operation operation, HandlerMethod handlerMethod) -> {
			ApiErrorCodeExamples apiErrorCodeExamples = handlerMethod.getMethodAnnotation(ApiErrorCodeExamples.class);
			if (apiErrorCodeExamples != null) {
				generateErrorCodeResponseExample(
					operation,
					apiErrorCodeExamples.value(),
					apiErrorCodeExamples.include(),
					apiErrorCodeExamples.isValidationError()
				);
			} else {
				ApiErrorCodeExample apiErrorCodeExample = handlerMethod.getMethodAnnotation(ApiErrorCodeExample.class);
				if (apiErrorCodeExample != null) {
					generateErrorCodeResponseExample(
						operation,
						apiErrorCodeExample.value(),
						apiErrorCodeExample.include(),
						apiErrorCodeExample.isValidationError()
					);
				}
			}
			return operation;
		};
	}

	/**
	 * 단일 Enum 클래스 처리 (@ApiErrorCodeExample 대응)
	 */
	private void generateErrorCodeResponseExample(
		Operation operation,
		Class<? extends ErrorCode> type,
		String[] include,
		boolean isValidationError
	) {
		ApiResponses responses = operation.getResponses();
		ErrorCode[] errorCodes = type.getEnumConstants();

		Map<Integer, List<ExampleHolder>> statusWithExampleHolders = Arrays.stream(errorCodes)
			.filter(e -> include.length == 0 || Arrays.asList(include).contains(e.name()))
			.map(e -> ExampleHolder.builder()
				.holder(getSwaggerExample(e, isValidationError))
				.code(e.getStatus().value())
				.name(e.name())
				.build())
			.collect(Collectors.groupingBy(ExampleHolder::getCode));

		addExamplesToResponses(responses, statusWithExampleHolders);
	}

	/**
	 * 다중 Enum 클래스 처리 (@ApiErrorCodeExamples 대응)
	 */
	private void generateErrorCodeResponseExample(
		Operation operation,
		Class<? extends ErrorCode>[] types,
		String[] include,
		boolean isValidationError
	) {
		ApiResponses responses = operation.getResponses();

		List<ExampleHolder> exampleHolders = Arrays.stream(types)
			.flatMap(type -> Arrays.stream(type.getEnumConstants())
				.filter(e -> include.length == 0 || Arrays.asList(include).contains(e.name())))
			.map(e -> ExampleHolder.builder()
				.holder(getSwaggerExample(e, isValidationError))
				.code(e.getStatus().value())
				.name(e.name())
				.build())
			.toList();

		Map<Integer, List<ExampleHolder>> statusWithExampleHolders = exampleHolders.stream()
			.collect(Collectors.groupingBy(ExampleHolder::getCode));

		addExamplesToResponses(responses, statusWithExampleHolders);
	}

	private void addExamplesToResponses(ApiResponses responses,
		Map<Integer, List<ExampleHolder>> statusWithExampleHolders) {

		statusWithExampleHolders.forEach((status, v) -> {
			Content content = new Content();
			MediaType mediaType = new MediaType();
			ApiResponse apiResponse = new ApiResponse();

			v.forEach(exampleHolder -> {
				mediaType.addExamples(exampleHolder.getName(), exampleHolder.getHolder());
			});

			content.addMediaType("application/json", mediaType);
			apiResponse.setContent(content);
			responses.addApiResponse(status.toString(), apiResponse);
		});
	}

	private Example getSwaggerExample(ErrorCode errorCode, boolean isValidationError) {
		ErrorResponse errorResponse;

		if (isValidationError) {
			// Validation 에러 케이스: FieldError 포함
			List<ErrorResponse.FieldError> fieldErrors = List.of(
				ErrorResponse.FieldError.of("field", "value", "")
			);
			errorResponse = ErrorResponse.of(errorCode.getMessage(), fieldErrors);
		} else {
			errorResponse = ErrorResponse.of(errorCode.getMessage());
		}

		Example example = new Example();
		example.setValue(errorResponse);
		return example;
	}
}
