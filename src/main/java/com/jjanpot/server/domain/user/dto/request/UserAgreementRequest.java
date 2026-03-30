package com.jjanpot.server.domain.user.dto.request;

import jakarta.validation.constraints.NotNull;

public record UserAgreementRequest(
	@NotNull
	Boolean ageVerified,
	@NotNull
	Boolean termsOfServiceAgreed,
	@NotNull
	Boolean privacyPolicyAgreed,
	Boolean marketingConsent
) {
}
