package io.mindsync.users.infrastructure.http

import io.kotest.assertions.print.print
import io.mindsync.KeycloakTestContainers
import io.mindsync.users.domain.Credential
import net.datafaker.Faker
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.MediaType
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf
import org.springframework.test.web.reactive.server.WebTestClient

private const val ENDPOINT = "/api/register"

/**
 *
 * @author Yuniel Acosta (acosta)
 * @created 6/7/23
 */
@AutoConfigureWebTestClient
class UserRegisterControllerTest : KeycloakTestContainers() {
    private val faker = Faker()

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    fun `should not register a new user without csrf token`() {
        webTestClient.post()
            .uri(ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                """
                {
                    "username": "${faker.name().username()}",
                    "email": "${faker.internet().emailAddress()}",
                    "password": "${Credential.generateRandomCredentialPassword()}",
                    "firstname": "${faker.name().firstName()}",
                    "lastname": "${faker.name().lastName()}"
                }
                """.trimIndent()
            )
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun `should register a new user`() {
        webTestClient
            .mutateWith(csrf())
            .post()
            .uri(ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                """
                {
                    "username": "${faker.name().username()}",
                    "email": "${faker.internet().emailAddress()}",
                    "password": "${Credential.generateRandomCredentialPassword()}",
                    "firstname": "${faker.name().firstName()}",
                    "lastname": "${faker.name().lastName()}"
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
    fun `should not register a new user with an existing username`() {
        val username = faker.name().username()
        val email = faker.internet().emailAddress()
        val password = Credential.generateRandomCredentialPassword()
        val firstname = faker.name().firstName()
        val lastname = faker.name().lastName()

        webTestClient
            .mutateWith(csrf())
            .post()
            .uri(ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                """
                {
                    "username": "$username",
                    "email": "$email",
                    "password": "$password",
                    "firstname": "$firstname",
                    "lastname": "$lastname"
                }
                """.trimIndent()
            )
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith {
                println(it.responseBody?.print())
            }

        webTestClient
            .mutateWith(csrf())
            .post()
            .uri(ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                """
                {
                    "username": "$username",
                    "email": "${faker.internet().emailAddress()}",
                    "password": "${Credential.generateRandomCredentialPassword()}",
                    "firstname": "${faker.name().firstName()}",
                    "lastname": "${faker.name().lastName()}"
                }
                """.trimIndent()
            )
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .consumeWith {
                println(it.responseBody?.print())
            }
    }

    @Test
    fun `should not register a new user with an existing email`() {
        val username = faker.name().username()
        val email = faker.internet().emailAddress()
        val password = Credential.generateRandomCredentialPassword()
        val firstname = faker.name().firstName()
        val lastname = faker.name().lastName()

        webTestClient
            .mutateWith(csrf())
            .post()
            .uri(ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                """
                {
                    "username": "$username",
                    "email": "$email",
                    "password": "$password",
                    "firstname": "$firstname",
                    "lastname": "$lastname"
                }
                """.trimIndent()
            )
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith {
                println(it.responseBody?.print())
            }

        webTestClient
            .mutateWith(csrf())
            .post()
            .uri(ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                """
                {
                    "username": "${faker.name().username()}",
                    "email": "$email",
                    "password": "${Credential.generateRandomCredentialPassword()}",
                    "firstname": "${faker.name().firstName()}",
                    "lastname": "${faker.name().lastName()}"
                }
                """.trimIndent()
            )
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .consumeWith {
                println(it.responseBody?.print())
            }
    }

    @Test
    fun `should not register a new user with an invalid email`() {
        webTestClient
            .mutateWith(csrf())
            .post()
            .uri(ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                """
                {
                    "username": "${faker.name().username()}",
                    "email": "invalid-email",
                    "password": "${Credential.generateRandomCredentialPassword()}",
                    "firstname": "${faker.name().firstName()}",
                    "lastname": "${faker.name().lastName()}"
                }
                """.trimIndent()
            )
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .consumeWith {
                println(it.responseBody?.print())
            }
    }

    @Test
    fun `should not register a new user with an invalid password`() {
        webTestClient
            .mutateWith(csrf())
            .post()
            .uri(ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                """
                {
                    "username": "${faker.name().username()}",
                    "email": "${faker.internet().emailAddress()}",
                    "password": "invalid-password",
                    "firstname": "${faker.name().firstName()}",
                    "lastname": "${faker.name().lastName()}"
                }
                """.trimIndent()
            )
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .consumeWith {
                println(it.responseBody?.print())
            }
    }

    @Test
    fun `should not register a new user with an invalid firstname`() {
        webTestClient
            .mutateWith(csrf())
            .post()
            .uri(ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                """
                {
                    "username": "${faker.name().username()}",
                    "email": "${faker.internet().emailAddress()}",
                    "password": "${Credential.generateRandomCredentialPassword()}",
                    "firstname": "",
                    "lastname": "${faker.name().lastName()}"
                }
                """.trimIndent()
            )
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .consumeWith {
                println(it.responseBody?.print())
            }
    }

}
