package io.mindsync.authentication.infrastructure.http

import io.mindsync.UnitTest
import io.mindsync.authentication.application.AuthenticateUserQueryHandler
import io.mindsync.authentication.application.UserAuthenticatorService
import io.mindsync.authentication.domain.AccessToken
import io.mindsync.authentication.domain.UserAuthenticator
import io.mindsync.authentication.infrastructure.http.request.LoginRequest
import io.mindsync.error.GlobalExceptionHandler
import io.mindsync.users.domain.exceptions.UserAuthenticationException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import net.datafaker.Faker
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.web.reactive.server.WebTestClient

private const val ENDPOINT = "/api/login"

@UnitTest
class UserAuthenticatorControllerTest {

    private val faker = Faker()
    private val accessToken = createAccessToken()

    private val userAuthenticator = mockk<UserAuthenticator>()
    private val authenticator = UserAuthenticatorService(userAuthenticator)
    private val authenticateUserQueryHandler = AuthenticateUserQueryHandler(authenticator)
    private val userAuthenticatorController = UserAuthenticatorController(authenticateUserQueryHandler)
    private val webTestClient = WebTestClient.bindToController(userAuthenticatorController)
        .controllerAdvice(GlobalExceptionHandler()) // Attach the global exception handler
        .build()

    @Test
    @DisplayName("should return access token when user logs in")
    fun `should return access token when user logs in`(): Unit = runBlocking {
        // Arrange
        val loginRequest = createLoginRequest()

        coEvery { userAuthenticator.authenticate(any(), any()) } returns accessToken

        // Act & Assert
        webTestClient.post().uri(ENDPOINT)
            .bodyValue(loginRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.token").isEqualTo(accessToken.token)
            .jsonPath("$.expiresIn").isEqualTo(accessToken.expiresIn)
            .jsonPath("$.refreshToken").isEqualTo(accessToken.refreshToken)
            .jsonPath("$.refreshExpiresIn").isEqualTo(accessToken.refreshExpiresIn)
            .jsonPath("$.tokenType").isEqualTo(accessToken.tokenType)
            .jsonPath("$.notBeforePolicy").isEqualTo(accessToken.notBeforePolicy!!)
            .jsonPath("$.sessionState").isEqualTo(accessToken.sessionState!!)
            .jsonPath("$.scope").isEqualTo(accessToken.scope!!)

        // Verify
        coVerify { userAuthenticator.authenticate(any(), any()) }
    }

    @Test
    @DisplayName("should handle UserAuthenticationException")
    fun `should handle UserAuthenticationException`(): Unit = runBlocking {
        // Arrange
        val loginRequest = createLoginRequest()

        coEvery {
            userAuthenticator.authenticate(any(), any())
        } throws UserAuthenticationException("Invalid account. User probably hasn't verified email.")

        // Act & Assert
        webTestClient.post().uri(ENDPOINT)
            .bodyValue(loginRequest)
            .exchange()
            .expectStatus().isUnauthorized
            .expectBody()
            .jsonPath("$.title").isEqualTo("User authentication failed")

        // Verify
        coVerify { userAuthenticator.authenticate(any(), any()) }
    }

    private fun createLoginRequest(
        username: String = faker.internet().emailAddress(),
        password: String = faker.internet().password(8, 80, true, true, true)
    ): LoginRequest = LoginRequest(
        username = username,
        password = password
    )

    private fun createAccessToken(): AccessToken = AccessToken(
        token = "token",
        expiresIn = 1L,
        refreshToken = "refreshToken",
        refreshExpiresIn = 1L,
        tokenType = "tokenType",
        notBeforePolicy = 1,
        sessionState = "sessionState",
        scope = "scope"
    )
}
