package io.mindsync.authentication.infrastructure.http

import io.kotest.assertions.print.print
import io.mindsync.authentication.domain.AccessToken
import io.mindsync.testcontainers.InfrastructureTestContainers
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.MediaType
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import kotlin.test.Test

private const val ENDPOINT = "/api/refresh-token"

/**
 *
 * @created 27/8/23
 */
@AutoConfigureWebTestClient
class RefreshTokenControllerIntegrationTest : InfrastructureTestContainers() {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    private val email = "john.doe@mindsync.com"
    private val password = "S3cr3tP@ssw0rd*123"
    private var accessToken: AccessToken? = null

    @BeforeEach
    fun setUp() {
        startInfrastructure()
        val returnResult = webTestClient
            .mutateWith(csrf())
            .post()
            .uri("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                """
                {
                    "username": "$email",
                    "password": "$password"
                }
                """.trimIndent()
            )
            .exchange()
            .returnResult<AccessToken>()
        accessToken = returnResult.responseBody.blockFirst()
    }

    @Test
    fun `should refresh token`() {
        webTestClient
            .mutateWith(csrf())
            .post()
            .uri(ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                """
                    {
                    "refreshToken": "${accessToken?.refreshToken}"
                    }
                """.trimIndent()
            )
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.token").isNotEmpty
            .jsonPath("$.expiresIn").isNotEmpty
            .jsonPath("$.refreshToken").isNotEmpty
            .jsonPath("$.refreshExpiresIn").isNotEmpty
            .jsonPath("$.tokenType").isNotEmpty
            .jsonPath("$.notBeforePolicy").isNotEmpty
            .jsonPath("$.sessionState").isNotEmpty
            .jsonPath("$.scope").isNotEmpty
            .consumeWith {
                println(it.responseBody?.print())
            }
    }
}
