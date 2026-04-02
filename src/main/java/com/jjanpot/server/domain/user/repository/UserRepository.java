package com.jjanpot.server.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jjanpot.server.domain.user.entity.Provider;
import com.jjanpot.server.domain.user.entity.User;

import jakarta.persistence.LockModeType;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByProviderAndProviderId(Provider provider, String providerId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT u FROM User u WHERE u.userId = :userId")
	Optional<User> findByIdForUpdate(@Param("userId") Long userId);
}
