package io.mindsync.authentication.infrastructure.persistence.keycloak

import io.mindsync.IntegrationTest
import io.mindsync.authentication.domain.RefreshToken
import io.mindsync.authentication.domain.RefreshTokenManager
import io.mindsync.testcontainers.InfrastructureTestContainers
import io.mindsync.users.domain.exceptions.UserRefreshTokenException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.Test
import kotlin.test.assertNotNull

@IntegrationTest
class KeycloakRefreshTokenManagerRepositoryIntegrationTest : InfrastructureTestContainers() {

    @Autowired
    private lateinit var refreshTokenManager: RefreshTokenManager

    @BeforeEach
    fun setUp() {
        startInfrastructure()
    }

    @Test
    fun `should refresh access token`(): Unit = runBlocking {
        val accessToken = getAdminAccessToken()
        val refreshToken = RefreshToken(accessToken?.refreshToken ?: "fake refresh token")
        val newAccessToken = refreshTokenManager.refresh(refreshToken)
        assertNotNull(newAccessToken)
        assertNotNull(newAccessToken.token)
        assertNotNull(newAccessToken.refreshToken)
        assertNotNull(newAccessToken.expiresIn)
        assertNotNull(newAccessToken.refreshExpiresIn)
        assertNotNull(newAccessToken.tokenType)
        assertNotNull(newAccessToken.scope)
        assertNotNull(newAccessToken.notBeforePolicy)
        assertNotNull(newAccessToken.sessionState)
        assertNotEquals(accessToken?.token, newAccessToken.token)
        assertNotEquals(accessToken?.refreshToken, newAccessToken.refreshToken)
    }

    @Test
    fun `should return exception when refresh access token`(): Unit = runBlocking {
        val refreshToken = RefreshToken("refreshToken")
        val exception = assertThrows(UserRefreshTokenException::class.java) {
            runBlocking { refreshTokenManager.refresh(refreshToken) }
        }
        assertNotNull(exception)
        assertEquals("Could not refresh access token", exception.message)
    }
}
