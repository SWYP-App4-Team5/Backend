package com.jjanpot.server.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jjanpot.server.domain.user.entity.Provider;
import com.jjanpot.server.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByProviderAndProviderId(Provider provider, String providerId);
}
