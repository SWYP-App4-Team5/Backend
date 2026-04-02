package com.jjanpot.server.domain.terms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjanpot.server.domain.terms.dto.TermsResponse;
import com.jjanpot.server.domain.terms.repository.TermsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TermsService {

	private final TermsRepository termsRepository;

	/** 약관 목록 조회 **/
	public List<TermsResponse> getTerms() {
		return termsRepository.findAllByOrderByCreatedAtAsc()
			.stream()
			.map(TermsResponse::from)
			.toList();
	}
}
