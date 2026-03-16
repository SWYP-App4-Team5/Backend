package com.jjanpot.server.domain.auth.entity;

import java.time.LocalDateTime;

import com.jjanpot.server.domain.user.entity.User;
import com.jjanpot.server.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "refresh_token",
	indexes = {
		@Index(name = "idx_refresh_token_user_id", columnList = "user_id"),
		@Index(name = "idx_refresh_token_token", columnList = "token")
	}
)
public class RefreshToken extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "refresh_token_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)) //FK 제약조건 안줌
	private User user;

	@Column(nullable = false, unique = true)
	private String token;

	@Column(nullable = false)
	private LocalDateTime expiresAt;

	public static RefreshToken createRefreshToken(
		User user, String token, LocalDateTime expiresAt
	) {
		return RefreshToken.builder()
			.user(user)
			.token(token)
			.expiresAt(expiresAt)
			.build();
	}

	public boolean isExpired() {
		return LocalDateTime.now().isAfter(expiresAt);
	}

	public void updateToken(String newRefreshToken, LocalDateTime newExpiresAt) {
		this.token = newRefreshToken;
		this.expiresAt = newExpiresAt;
	}

}
