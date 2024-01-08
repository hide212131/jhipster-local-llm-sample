package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.security.jwt.JwtAuthenticationTestUtils.createValidTokenForUser;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.security.AuthoritiesConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link AccountResource} REST controller.
 */
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_TIMEOUT)
@IntegrationTest
class AccountResourceIT {

    static final String TEST_USER_LOGIN = "test";

    @Autowired
    private WebTestClient accountWebTestClient;

    @Value("${jhipster.security.authentication.jwt.base64-secret}")
    private String jwtKey;

    @Test
    void testGetExistingAccount() {
        accountWebTestClient
            .get()
            .uri("/api/account")
            .accept(MediaType.APPLICATION_JSON)
            .headers(header -> header.setBearerAuth(createValidTokenForUser(jwtKey, TEST_USER_LOGIN)))
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.login")
            .isEqualTo(TEST_USER_LOGIN)
            .jsonPath("$.authorities")
            .isEqualTo(AuthoritiesConstants.ADMIN);
    }

    @Test
    @WithUnauthenticatedMockUser
    void testNonAuthenticatedUser() {
        accountWebTestClient
            .get()
            .uri("/api/authenticate")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .isEmpty();
    }

    @Test
    @WithMockUser(TEST_USER_LOGIN)
    void testAuthenticatedUser() {
        accountWebTestClient
            .get()
            .uri("/api/authenticate")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .isEqualTo(TEST_USER_LOGIN);
    }
}
