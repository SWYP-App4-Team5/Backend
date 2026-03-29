package com.jjanpot.server.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jjanpot.server.domain.user.entity.User;
import com.jjanpot.server.domain.user.entity.UserAgreement;

@Repository
public interface UserAgreementRepository extends JpaRepository<UserAgreement, Long> {
	boolean existsByUser(User user);
}
