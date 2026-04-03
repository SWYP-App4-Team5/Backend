package com.jjanpot.server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.google.firebase.FirebaseApp;

@ActiveProfiles("test")
@SpringBootTest
class ServerApplicationTests {

	@MockitoBean
	private FirebaseApp firebaseApp;

	@Test
	void contextLoads() {
	}

}
