package io.mindsync.authentication.infrastructure.persistence.keycloak

import io.mindsync.CredentialGenerator
import io.mindsync.IntegrationTest
import io.mindsync.authentication.domain.UserAuthenticator
import io.mindsync.authentication.domain.Username
import io.mindsync.testcontainers.InfrastructureTestContainers
import io.mindsync.users.domain.User
import io.mindsync.users.domain.UserCreator
import io.mindsync.users.domain.exceptions.UserAuthenticationException
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import net.datafaker.Faker
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@IntegrationTest
class KeycloakAuthenticatorRepositoryIntegrationTest : InfrastructureTestContainers() {
    // this user is created by default in Keycloak container (see demo-realm-test.json)
    private val email = "john.doe@mindsync.com"
    private val username = "john.doe"
    private val password = "S3cr3tP@ssw0rd*123"

    private val faker = Faker()

    @Autowired
    private lateinit var userCreator: UserCreator

    @Autowired
    private lateinit var userAuthenticator: UserAuthenticator

    @BeforeEach
    fun setUp() {
        startInfrastructure()
    }

    @Test
    fun `should authenticate a user by email`(): Unit = runBlocking {
        val username = Username(email)
        val credential = CredentialGenerator.generate(password)
        val accessToken = userAuthenticator.authenticate(username, credential)
        assertNotNull(accessToken)
        assertNotNull(accessToken.token)
        assertNotNull(accessToken.refreshToken)
        assertNotNull(accessToken.expiresIn)
        assertNotNull(accessToken.refreshExpiresIn)
        assertNotNull(accessToken.tokenType)
        assertNotNull(accessToken.scope)
        assertNotNull(accessToken.notBeforePolicy)
        assertNotNull(accessToken.sessionState)
    }

    @Test
    fun `should authenticate a user by username`(): Unit = runBlocking {
        val username = Username(username)
        val credential = CredentialGenerator.generate(password)
        val accessToken = userAuthenticator.authenticate(username, credential)
        assertNotNull(accessToken)
        assertNotNull(accessToken.token)
        assertNotNull(accessToken.refreshToken)
        assertNotNull(accessToken.expiresIn)
        assertNotNull(accessToken.refreshExpiresIn)
        assertNotNull(accessToken.tokenType)
        assertNotNull(accessToken.scope)
        assertNotNull(accessToken.notBeforePolicy)
        assertNotNull(accessToken.sessionState)
    }

    @Test
    fun `should throw exception when user is not found`(): Unit = runBlocking {
        val username = Username("not.found")
        val credential = CredentialGenerator.generate(password)
        val exception = assertThrows(UserAuthenticationException::class.java) {
            runBlocking {
                userAuthenticator.authenticate(username, credential)
            }
        }
        assertEquals("Invalid account. User probably hasn't verified email.", exception.message)
    }

    @Test
    fun `should throw exception when password is wrong`(): Unit = runBlocking {
        val username = Username(email)
        val credential = CredentialGenerator.generate("${password}wrong")
        val exception = assertThrows(UserAuthenticationException::class.java) {
            runBlocking {
                userAuthenticator.authenticate(username, credential)
            }
        }
        assertEquals("Invalid account. User probably hasn't verified email.", exception.message)
    }

    @Test
    fun `should throw exception when user is not verified`(): Unit = runBlocking {
        val randomEmail = faker.internet().emailAddress()
        val randomPassword = faker.internet().password(8, 80, true, true, true)
        val user = User.create(
            email = randomEmail,
            firstName = faker.name().firstName(),
            lastName = faker.name().lastName(),
            password = randomPassword
        )
        val createdUser = userCreator.create(user).awaitSingle()
        assertNotNull(createdUser)
        val username = Username(randomEmail)
        val credential = CredentialGenerator.generate(randomPassword)
        val exception = assertThrows(UserAuthenticationException::class.java) {
            runBlocking {
                userAuthenticator.authenticate(username, credential)
            }
        }
        assertEquals("Invalid account. User probably hasn't verified email.", exception.message)
    }
}
