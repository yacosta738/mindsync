package io.mindsync.authentication.infrastructure.http

import io.kotest.assertions.print.print
import io.mindsync.CredentialGenerator
import io.mindsync.testcontainers.InfrastructureTestContainers
import io.mindsync.users.domain.Credential
import io.mindsync.users.domain.User
import io.mindsync.users.domain.UserCreator
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import net.datafaker.Faker
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.MediaType
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

private const val ENDPOINT = "/api/login"

private const val TITLE = "User authentication failed"

private const val DETAIL = "Unable to authenticate user"

private const val ERROR_CATEGORY = "AUTHENTICATION"

@AutoConfigureWebTestClient
class UserAuthenticatorControllerTest : InfrastructureTestContainers() {
    private val faker = Faker()
    private var usernameOrEmail = "admin@mindsync.com"
    private val password = "S3cr3tP@ssw0rd*123"

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var userCreator: UserCreator

    @BeforeEach
    fun setUp() {
        startInfrastructure()
        usernameOrEmail = faker.internet().emailAddress()
        runBlocking {
            createUser(email = usernameOrEmail, password = password).also {
                println("\uD83E\uDDEA TEST: User create for test purpose: $it")
            }
        }
    }

    @Test
    fun `should not authenticate a user without csrf token`() {
        webTestClient.post()
            .uri(ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun `should authenticate a user`() {
        webTestClient
            .mutateWith(csrf())
            .post()
            .uri(ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                """
                {
                    "username": "$usernameOrEmail",
                    "password": "$password"
                }
                """.trimIndent()
            )
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith {
                println(it.responseBody?.print())
            }
    }

    @Test
    fun `should not authenticate a user with invalid credentials`() {
        webTestClient
            .mutateWith(csrf())
            .post()
            .uri(ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                """
                {
                    "username": "$usernameOrEmail",
                    "password": "${password}invalidPassword"
                }
                """.trimIndent()
            )
            .exchange()
            .expectStatus().isUnauthorized
            .expectBody()
            .jsonPath("$.title").isEqualTo(TITLE)
            .jsonPath("$.detail").isEqualTo(DETAIL)
            .jsonPath("$.instance").isEqualTo(ENDPOINT)
            .jsonPath("$.errorCategory").isEqualTo(ERROR_CATEGORY)
            .jsonPath("$.timestamp").isNotEmpty
    }

    @Test
    fun `should not authenticate a user with invalid username`() {
        webTestClient
            .mutateWith(csrf())
            .post()
            .uri(ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                """
                {
                    "username": "${usernameOrEmail}invalidUsername",
                    "password": "$password"
                }
                """.trimIndent()
            )
            .exchange()
            .expectStatus().isUnauthorized
            .expectBody()
            .jsonPath("$.title").isEqualTo(TITLE)
            .jsonPath("$.detail").isEqualTo(DETAIL)
            .jsonPath("$.instance").isEqualTo(ENDPOINT)
            .jsonPath("$.errorCategory").isEqualTo(ERROR_CATEGORY)
            .jsonPath("$.timestamp").isNotEmpty
    }

    private suspend fun createUser(
        email: String = faker.internet().emailAddress(),
        password: String = Credential.generateRandomCredentialPassword(),
        firstName: String = faker.name().firstName(),
        lastName: String = faker.name().lastName()
    ): User {
        val user = User(
            id = UUID.randomUUID(),
            email = email,
            credentials = mutableListOf(CredentialGenerator.generate(password)),
            firstName = firstName,
            lastName = lastName
        )
        return userCreator.create(user).awaitSingle()
    }
}
