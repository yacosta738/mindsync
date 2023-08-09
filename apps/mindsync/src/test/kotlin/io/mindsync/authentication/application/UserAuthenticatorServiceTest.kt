package io.mindsync.authentication.application

import io.mindsync.CredentialGenerator
import io.mindsync.UnitTest
import io.mindsync.authentication.domain.Username
import io.mindsync.users.domain.exceptions.UserAuthenticationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.core.context.SecurityContextHolder

private const val PASSWORD = "My5up3rS3cr3tP@ssw0rd"

private const val USER = "user"

@UnitTest
@OptIn(ExperimentalCoroutinesApi::class)
class UserAuthenticatorServiceTest {
    private val inMemoryUserAuthenticator = InMemoryUserAuthenticator()
    private val userAuthenticatorService: UserAuthenticatorService = UserAuthenticatorService(inMemoryUserAuthenticator)

    @BeforeEach
    fun setUp() {
        SecurityContextHolder.clearContext()
        inMemoryUserAuthenticator.addUser(USER, PASSWORD)
    }

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
        inMemoryUserAuthenticator.clear()
    }

    @Test
    fun `should authenticate a user`() = runTest {
        val accessToken = userAuthenticatorService.authenticate(
            Username(USER),
            CredentialGenerator.generate(PASSWORD)
        )
        assertNotNull(accessToken)
        assertEquals("token", accessToken.token)
        assertEquals(1, accessToken.expiresIn)
        assertEquals("refreshToken", accessToken.refreshToken)
        assertEquals(1, accessToken.refreshExpiresIn)
        assertEquals("type", accessToken.tokenType)
        assertEquals(1, accessToken.notBeforePolicy)
        assertEquals("81945a53-7bfa-4347-9321-b46c2a2a736d", accessToken.sessionState)
        assertEquals("email profile", accessToken.scope)
    }

    @Test
    fun `should throw exception when user is not found`() = runTest {
        val exception = assertThrows(UserAuthenticationException::class.java) {
            runBlocking {
                userAuthenticatorService.authenticate(
                    Username(USER),
                    CredentialGenerator.generate("wr0ngP4ssw0rd*86632")
                )
            }
        }
        assertEquals("Invalid account. User probably hasn't verified email.", exception.message)
    }
}
