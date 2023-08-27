package io.mindsync.authentication.infrastructure.http

import io.mindsync.UnitTest
import io.mindsync.authentication.application.RefreshTokenQueryHandler
import io.mindsync.authentication.domain.AccessToken
import io.mindsync.authentication.domain.RefreshToken
import io.mindsync.authentication.domain.RefreshTokenManager
import io.mindsync.authentication.infrastructure.http.request.RefreshTokenRequest
import io.mindsync.error.GlobalExceptionHandler
import io.mindsync.users.domain.exceptions.UserRefreshTokenException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import net.datafaker.Faker
import org.junit.jupiter.api.Assertions.*
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.Test

private const val ENDPOINT = "/api/refresh-token"

@UnitTest
class RefreshTokenControllerTest {

    private val faker = Faker()
    private val accessToken = createAccessToken()

    private val refreshTokenManager = mockk<RefreshTokenManager>()
    private val refreshTokenQueryHandler = RefreshTokenQueryHandler(refreshTokenManager)
    private val refreshTokenController = RefreshTokenController(refreshTokenQueryHandler)
    private val webTestClient = WebTestClient.bindToController(refreshTokenController)
        .controllerAdvice(GlobalExceptionHandler()) // Attach the global exception handler
        .build()

    @Test
    fun `should refresh token`(): Unit = runBlocking {
        // Arrange
        val refreshTokenRequest = createRefreshTokenRequest()

        coEvery { refreshTokenManager.refresh(any(RefreshToken::class)) } returns accessToken

        // Act & Assert
        webTestClient.post().uri(ENDPOINT)
            .bodyValue(refreshTokenRequest)
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
        coVerify { refreshTokenManager.refresh(any()) }
    }

    @Test
    fun `should return bad request when refresh token is not provided`(): Unit = runBlocking {
        // Arrange
        val refreshTokenRequest = RefreshTokenRequest(refreshToken = "")

        coEvery {
            refreshTokenManager.refresh(any(RefreshToken::class))
        } throws UserRefreshTokenException("Could not refresh access token")

        // Act & Assert
        webTestClient.post().uri(ENDPOINT)
            .bodyValue(refreshTokenRequest)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.title").isEqualTo("Bad Request")
            .jsonPath("$.status").isEqualTo(400)
            .jsonPath("$.detail").isEqualTo("Invalid request content.")

        // Verify
        coVerify(exactly = 0) { refreshTokenManager.refresh(any()) }
    }

    private fun createRefreshTokenRequest(): RefreshTokenRequest = RefreshTokenRequest(
        refreshToken = faker.lorem().characters(10)
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
