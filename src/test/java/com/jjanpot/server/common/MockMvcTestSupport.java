package com.jjanpot.server.common;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jjanpot.server.global.exception.GlobalExceptionHandler;

public abstract class MockMvcTestSupport {

	protected MockMvc mockMvc;

	protected ObjectMapper objectMapper = new ObjectMapper()
		.registerModule(new JavaTimeModule())
		.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

	@BeforeEach
	void setUp() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(initController())
			.setControllerAdvice(new GlobalExceptionHandler())
			.setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
			.build();
	}

	protected abstract Object initController();
}
