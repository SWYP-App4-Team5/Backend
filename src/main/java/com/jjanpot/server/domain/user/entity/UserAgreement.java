package com.jjanpot.server.domain.user.entity;

import com.jjanpot.server.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "user_agreement")
public class UserAgreement extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_agreement_id", nullable = false)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	//만 14세이상 여부
	@Builder.Default
	@Column(name = "age_verified", nullable = false, columnDefinition = "TINYINT(1)")
	private boolean ageVerified = false;

	//서비스 이용약관 동의 여부
	@Builder.Default
	@Column(name = "terms_of_service_agreed", nullable = false, columnDefinition = "TINYINT(1)")
	private boolean termsOfServiceAgreed = false;

	//개인정보 처리 동의
	@Builder.Default
	@Column(name = "privacy_policy_agreed", nullable = false, columnDefinition = "TINYINT(1)")
	private boolean privacyPolicyAgreed = false;

	public static UserAgreement from(boolean ageVerified, boolean termsOfServiceAgreed, boolean privacyPolicyAgreed,
		User user) {
		return UserAgreement.builder()
			.ageVerified(ageVerified)
			.termsOfServiceAgreed(termsOfServiceAgreed)
			.privacyPolicyAgreed(privacyPolicyAgreed)
			.user(user)
			.build();
	}

}
