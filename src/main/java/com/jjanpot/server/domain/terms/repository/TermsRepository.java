package com.jjanpot.server.domain.terms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jjanpot.server.domain.terms.entity.Terms;

public interface TermsRepository extends JpaRepository<Terms, Long> {

	List<Terms> findAllByOrderByCreatedAtAsc();
}
