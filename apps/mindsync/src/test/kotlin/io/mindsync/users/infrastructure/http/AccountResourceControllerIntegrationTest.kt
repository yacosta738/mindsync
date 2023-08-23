package io.mindsync.users.infrastructure.http

import io.mindsync.testcontainers.InfrastructureTestContainers
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOAuth2Login
import org.springframework.test.web.reactive.server.WebTestClient

private const val ENDPOINT = "/api/account"

@AutoConfigureWebTestClient
class AccountResourceControllerIntegrationTest : InfrastructureTestContainers() {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    fun `should get account information successfully`() {
        webTestClient.mutateWith(
            oAuth2LoginMutator()
        )
            .get().uri(ENDPOINT)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.username").isEqualTo("test")
            .jsonPath("$.email").isEqualTo("test@localhost")
            .jsonPath("$.firstname").isEqualTo("Test")
            .jsonPath("$.lastname").isEqualTo("User")
            .jsonPath("$.authorities").isArray.jsonPath("$.authorities[0]")
            .isEqualTo("ROLE_USER")
    }

    @Test
    fun `should get account information successfully with multiple roles`() {
        webTestClient.mutateWith(
            oAuth2LoginMutator(roles = listOf("ROLE_USER", "ROLE_ADMIN"))
        )
            .get().uri(ENDPOINT)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.username").isEqualTo("test")
            .jsonPath("$.email").isEqualTo("test@localhost")
            .jsonPath("$.firstname").isEqualTo("Test")
            .jsonPath("$.lastname").isEqualTo("User")
            .jsonPath("$.authorities").isArray.jsonPath("$.authorities[0]")
            .isEqualTo("ROLE_USER")
            .jsonPath("$.authorities[1]")
            .isEqualTo("ROLE_ADMIN")
    }

    private fun oAuth2LoginMutator(
        username: String = "test",
        email: String = "test@localhost",
        firstname: String = "Test",
        lastname: String = "User",
        roles: List<String> = listOf("ROLE_USER")
    ): SecurityMockServerConfigurers.OAuth2LoginMutator =
        mockOAuth2Login().authorities(SimpleGrantedAuthority(roles.joinToString(separator = ",") { it })).attributes {
            it["preferred_username"] = username
            it["email"] = email
            it["given_name"] = firstname
            it["family_name"] = lastname
            it["roles"] = roles
        }
}
