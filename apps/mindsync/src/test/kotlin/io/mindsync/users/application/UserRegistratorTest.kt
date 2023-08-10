package io.mindsync.users.application

import io.kotest.common.runBlocking
import io.mindsync.UnitTest
import io.mindsync.users.application.command.RegisterUserCommand
import io.mindsync.users.domain.ApiDataResponse
import io.mindsync.users.domain.ApiResponseStatus
import io.mindsync.users.domain.Credential
import io.mindsync.users.domain.event.UserCreatedEvent
import net.datafaker.Faker
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono

/**
 *
 *
 * @created 3/7/23
 */
@UnitTest
class UserRegistratorTest {
    private val userRepository = InMemoryUserRepository()
    private val eventPublisher = InMemoryEventPublisher<UserCreatedEvent>()
    private val userRegistrator = UserRegistrator(userRepository, eventPublisher)
    private val faker = Faker()

    @Test
    fun `should register new user`() {
        val registerUserCommand = createRegisterUserCommand()

        runBlocking {
            val result: Mono<ApiDataResponse<UserResponse>> =
                userRegistrator.registerNewUser(registerUserCommand)

            val response = result.block()
            assertNotNull(response)
            val data = response?.data
            assertNotNull(data)
            assertEquals(registerUserCommand.email, data?.username)
            assertEquals(registerUserCommand.email, data?.email)
            assertEquals(registerUserCommand.firstname, data?.firstname)
            assertEquals(registerUserCommand.lastname, data?.lastname)
        }
    }

    @Test
    fun `should not register new user with wrong email`(): Unit = runBlocking {
        val invalidEmail = "test"
        val registerUserCommand = createRegisterUserCommand(email = invalidEmail)

        val result: Mono<ApiDataResponse<UserResponse>> =
            userRegistrator.registerNewUser(registerUserCommand)

        val response = result.block()
        assertNotNull(response)
        if (response != null) {
            assertEquals(response.status, ApiResponseStatus.FAILURE)
            assertNull(response.data)
            assertNotNull(response.error)
            assertEquals("The email <$invalidEmail> is not valid", response.error)
        }
    }

    @Test
    fun `should not register new user with wrong password`(): Unit = runBlocking {
        val invalidPassword = "ab@W"
        val registerUserCommand = createRegisterUserCommand(password = invalidPassword)

        val result: Mono<ApiDataResponse<UserResponse>> =
            userRegistrator.registerNewUser(registerUserCommand)

        val response = result.block()
        assertNotNull(response)
        if (response != null) {
            assertEquals(response.status, ApiResponseStatus.FAILURE)
            assertNull(response.data)
            assertNotNull(response.error)
            assertEquals("Credential value must be at least 8 characters", response.error)
        }
    }

    @Test
    fun `should not register new user with wrong firstname`(): Unit = runBlocking {
        val invalidFirstname = "ab!@#$%^&*()_+"
        val registerUserCommand = createRegisterUserCommand(firstname = invalidFirstname)

        val result: Mono<ApiDataResponse<UserResponse>> =
            userRegistrator.registerNewUser(registerUserCommand)

        val response = result.block()
        assertNotNull(response)
        if (response != null) {
            assertEquals(response.status, ApiResponseStatus.FAILURE)
            assertNull(response.data)
            assertNotNull(response.error)
            assertEquals("The first name <$invalidFirstname> is not valid", response.error)
        }
    }

    @Test
    fun `should not register new user with wrong lastname`(): Unit = runBlocking {
        val charUppercase = 'A'..'Z'
        val charLowercase = 'a'..'z'
        val invalidLastname = (charUppercase + charLowercase).shuffled().joinToString("").repeat(4)
        val registerUserCommand = createRegisterUserCommand(lastname = invalidLastname)

        val result: Mono<ApiDataResponse<UserResponse>> =
            userRegistrator.registerNewUser(registerUserCommand)

        val response = result.block()
        assertNotNull(response)
        if (response != null) {
            assertEquals(response.status, ApiResponseStatus.FAILURE)
            assertNull(response.data)
            assertNotNull(response.error)
            assertEquals("The last name <$invalidLastname> is not valid", response.error)
        }
    }

    @Test
    fun `should not register new user with existing email`(): Unit = runBlocking {
        val registerUserCommand = createRegisterUserCommand(email = "test@google.com")

        var result: Mono<ApiDataResponse<UserResponse>> =
            userRegistrator.registerNewUser(registerUserCommand)

        var response = result.block()
        assertNotNull(response)
        val data = response?.data
        assertNotNull(data)
        assertEquals(registerUserCommand.email, data?.username)
        assertEquals(registerUserCommand.email, data?.email)
        assertEquals(registerUserCommand.firstname, data?.firstname)
        assertEquals(registerUserCommand.lastname, data?.lastname)

        val registerUserCommand2 = createRegisterUserCommand(email = "test@google.com")

        result = userRegistrator.registerNewUser(registerUserCommand2)

        response = result.block()
        assertNotNull(response)
        if (response != null) {
            assertEquals(response.status, ApiResponseStatus.FAILURE)
            assertNull(response.data)
            assertNotNull(response.error)
            assertEquals("Failed to register new user. Please try again.", response.error)
        }
    }

    @Test
    fun `should produce user registered event`() {
        val registerUserCommand = createRegisterUserCommand()

        runBlocking {
            val result: Mono<ApiDataResponse<UserResponse>> =
                userRegistrator.registerNewUser(registerUserCommand)

            val response = result.block()
            assertNotNull(response)
            val data = response?.data
            assertNotNull(data)
            assertEquals(registerUserCommand.email, data?.username)
            assertEquals(registerUserCommand.email, data?.email)
            assertEquals(registerUserCommand.firstname, data?.firstname)
            assertEquals(registerUserCommand.lastname, data?.lastname)
        }

        val event = eventPublisher.getEvents().first()
        assertNotNull(event)
        assertEquals(UserCreatedEvent::class.java, event::class.java)
        assertEquals(registerUserCommand.email, event.username)
        assertEquals(registerUserCommand.email, event.email)
        assertEquals(registerUserCommand.firstname, event.firstname)
        assertEquals(registerUserCommand.lastname, event.lastname)
    }

    private fun createRegisterUserCommand(
        email: String = faker.internet().emailAddress(),
        password: String = Credential.generateRandomCredentialPassword(),
        firstname: String = faker.name().firstName(),
        lastname: String = faker.name().lastName()
    ) = RegisterUserCommand(
        email = email,
        password = password,
        firstname = firstname,
        lastname = lastname
    )
}
