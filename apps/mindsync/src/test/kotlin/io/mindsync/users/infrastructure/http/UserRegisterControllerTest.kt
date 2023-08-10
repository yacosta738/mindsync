package io.mindsync.users.infrastructure.http

import io.mindsync.UnitTest
import io.mindsync.users.application.UserRegistrator
import io.mindsync.users.application.UserResponse
import io.mindsync.users.domain.ApiDataResponse
import io.mindsync.users.domain.ApiResponseStatus
import io.mindsync.users.domain.exceptions.UserStoreException
import io.mindsync.users.infrastructure.dto.RegisterUserRequest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import net.datafaker.Faker
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import kotlin.test.Test

private const val ENDPOINT = "/api/register"

@UnitTest
class UserRegisterControllerTest {
    private val faker = Faker()
    private val email = faker.internet().emailAddress()
    private val password = faker.internet().password(8, 80, true, true, true)
    private val firstName = faker.name().firstName()
    private val lastName = faker.name().lastName()
    private val successResponse =
        UserResponse(username = email, email = email, firstname = firstName, lastname = lastName)

    private val mockUserRegistrator: UserRegistrator = mockk()
    private val userRegisterController = UserRegisterController(mockUserRegistrator)
    private val webTestClient = WebTestClient.bindToController(userRegisterController).build()

    @Test
    fun `should register user successfully`() = runBlocking {
        // Arrange
        val request = RegisterUserRequest(email, password, firstName, lastName)
        val expectedApiDataResponse = ApiDataResponse.success(successResponse)

        coEvery { mockUserRegistrator.registerNewUser(any()) } returns Mono.just(expectedApiDataResponse)

        // Act & Assert
        webTestClient.post().uri(ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("$.status").isEqualTo(ApiResponseStatus.SUCCESS.toString())
            .jsonPath("$.data.email").isEqualTo(request.email)

        // Verify
        coVerify { mockUserRegistrator.registerNewUser(any()) }
    }

    @Test
    fun `should handle failed registration`() = runBlocking {
        // Arrange
        val request = RegisterUserRequest(email, password, firstName, lastName)
        val expectedApiDataResponse = ApiDataResponse.failure<UserResponse>(
            "Failed to register new user. Please try again."
        )

        coEvery { mockUserRegistrator.registerNewUser(any()) } returns Mono.just(expectedApiDataResponse)

        // Act & Assert
        webTestClient.post().uri(ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.status").isEqualTo(ApiResponseStatus.FAILURE.toString())
            .jsonPath("$.data.email").doesNotExist()

        // Verify
        coVerify { mockUserRegistrator.registerNewUser(any()) }
    }

    @Test
    fun `should handle registration error`() = runBlocking {
        // Arrange
        val request = RegisterUserRequest(email, password, firstName, lastName)

        coEvery { mockUserRegistrator.registerNewUser(any()) } throws UserStoreException("Registration error")

        // Act & Assert
        webTestClient.post().uri(ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().is5xxServerError

        // Verify
        coVerify { mockUserRegistrator.registerNewUser(any()) }
    }
}
