package com.jjanpot.server.domain.terms.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjanpot.server.domain.terms.controller.docs.TermsControllerDocs;
import com.jjanpot.server.domain.terms.dto.TermsResponse;
import com.jjanpot.server.domain.terms.service.TermsService;
import com.jjanpot.server.global.common.dto.SuccessResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/terms/v1")
public class TermsController implements TermsControllerDocs {

	private final TermsService termsService;

	@GetMapping
	public SuccessResponse<List<TermsResponse>> getTerms() {
		return SuccessResponse.ok(termsService.getTerms());
	}
}
