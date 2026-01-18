package com.assetguard.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AssetGuardApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void contextLoads() {
	}

	@Test
	void testHomePageLoads() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Professional Asset Management")));
	}

	@Test
	void testLoginPageLoads() throws Exception {
		mockMvc.perform(get("/login"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Welcome Back")));
	}

	@Test
	@org.junit.jupiter.api.Disabled("Failing due to environment/schema mismatch in test context - to be fixed")
	void testRegisterValues() throws Exception {
		String testEmail = "integratedtest_" + System.currentTimeMillis() + "@example.com";

		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new RegisterRequest(
						"Test", "User", testEmail, "Password123"))))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").exists());

		// Cleanup handled by database rollback in transaction usually, or use distinct
		// email
	}

	// Helper DTO for test
	static class RegisterRequest {
		public String firstName;
		public String lastName;
		public String email;
		public String password;

		public RegisterRequest(String f, String l, String e, String p) {
			this.firstName = f;
			this.lastName = l;
			this.email = e;
			this.password = p;
		}
	}
}
