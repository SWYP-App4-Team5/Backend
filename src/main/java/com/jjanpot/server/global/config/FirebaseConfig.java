package com.jjanpot.server.global.config;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import jakarta.annotation.PostConstruct;

@Profile("!test")
@Configuration
public class FirebaseConfig {
	@Value("${custom.fcm.key-path:jjanpot-fcm.json}")
	private String keyPath;

	@PostConstruct
	public void init() throws IOException {
		ClassPathResource resource = new ClassPathResource(keyPath);

		try (InputStream serviceAccount = resource.getInputStream()) {
			FirebaseOptions firebaseOptions = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.build();

			if (FirebaseApp.getApps().isEmpty()) {
				FirebaseApp.initializeApp(firebaseOptions);
			}
		}
	}

	@Bean
	public FirebaseMessaging firebaseMessaging() {
		return FirebaseMessaging.getInstance();
	}
}
