package io.mindsync.users.application

import arrow.core.Either
import io.kotest.common.runBlocking
import io.mindsync.UnitTest
import io.mindsync.users.application.command.RegisterUserCommand
import io.mindsync.users.domain.Credential
import io.mindsync.users.domain.ApiResponse
import io.mindsync.users.domain.event.UserCreatedEvent
import io.mindsync.users.domain.exceptions.UserStoreException
import net.datafaker.Faker
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
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
            val result: Mono<Either<UserStoreException, ApiResponse<UserResponse>>> =
                userRegistrator.registerNewUser(registerUserCommand)

            val response = result.block()
            assertNotNull(response)
            assertTrue(response?.isRight() ?: false)
            val userResponse = response?.fold(
                { _ -> null },
                { uResponse -> uResponse }
            )
            assertNotNull(userResponse)
            val data = userResponse?.data
            assertNotNull(data)
            assertEquals(registerUserCommand.username, data?.username)
            assertEquals(registerUserCommand.email, data?.email)
            assertEquals(registerUserCommand.firstname, data?.firstname)
            assertEquals(registerUserCommand.lastname, data?.lastname)
        }
    }

    @Test
    fun `should not register new user with wrong email`() {
        val invalidEmail = "test"
        val registerUserCommand = createRegisterUserCommand(email = invalidEmail)

        runBlocking {
            val result: Mono<Either<UserStoreException, ApiResponse<UserResponse>>> =
                userRegistrator.registerNewUser(registerUserCommand)

            val response = result.block()
            assertNotNull(response)
            assertTrue(response?.isLeft() ?: false)
            val error = response?.fold(
                { error -> error },
                { _ -> null }
            )
            assertNotNull(error)
            assertEquals("The email <$invalidEmail> is not valid", error?.message)
        }
    }

    @Test
    fun `should not register new user with wrong username`() {
        val invalidUsername = "ab"
        val registerUserCommand = createRegisterUserCommand(username = invalidUsername)

        runBlocking {
            val result: Mono<Either<UserStoreException, ApiResponse<UserResponse>>> =
                userRegistrator.registerNewUser(registerUserCommand)

            val response = result.block()
            assertNotNull(response)
            assertTrue(response?.isLeft() ?: false)
            val error = response?.fold(
                { error -> error },
                { _ -> null }
            )
            assertNotNull(error)
            assertEquals("Username must be between 3 and 100 characters", error?.message)
        }
    }

    @Test
    fun `should not register new user with wrong password`() {
        val invalidPassword = "ab@W"
        val registerUserCommand = createRegisterUserCommand(password = invalidPassword)

        runBlocking {
            val result: Mono<Either<UserStoreException, ApiResponse<UserResponse>>> =
                userRegistrator.registerNewUser(registerUserCommand)

            val response = result.block()
            assertNotNull(response)
            assertTrue(response?.isLeft() ?: false)
            val error = response?.fold(
                { error -> error },
                { _ -> null }
            )
            assertNotNull(error)
            assertEquals("Credential value must be at least 8 characters", error?.message)
        }
    }

    @Test
    fun `should not register new user with wrong firstname`() {
        val invalidFirstname = "ab!@#$%^&*()_+"
        val registerUserCommand = createRegisterUserCommand(firstname = invalidFirstname)

        runBlocking {
            val result: Mono<Either<UserStoreException, ApiResponse<UserResponse>>> =
                userRegistrator.registerNewUser(registerUserCommand)

            val response = result.block()
            assertNotNull(response)
            assertTrue(response?.isLeft() ?: false)
            val error = response?.fold(
                { error -> error },
                { _ -> null }
            )
            assertNotNull(error)
            assertEquals("The first name <$invalidFirstname> is not valid", error?.message)
        }
    }

    @Test
    fun `should not register new user with wrong lastname`() {
        val charUppercase = 'A'..'Z'
        val charLowercase = 'a'..'z'
        val invalidLastname = (charUppercase + charLowercase).shuffled().joinToString("").repeat(4)
        val registerUserCommand = createRegisterUserCommand(lastname = invalidLastname)

        runBlocking {
            val result: Mono<Either<UserStoreException, ApiResponse<UserResponse>>> =
                userRegistrator.registerNewUser(registerUserCommand)

            val response = result.block()
            assertNotNull(response)
            assertTrue(response?.isLeft() ?: false)
            val error = response?.fold(
                { error -> error },
                { _ -> null }
            )
            assertNotNull(error)
            assertEquals("The last name <$invalidLastname> is not valid", error?.message)
        }
    }

    @Test
    fun `should not register new user with existing username`() {
        val registerUserCommand = createRegisterUserCommand(username = "test")

        runBlocking {
            val result: Mono<Either<UserStoreException, ApiResponse<UserResponse>>> =
                userRegistrator.registerNewUser(registerUserCommand)

            val response = result.block()
            assertNotNull(response)
            assertTrue(response?.isRight() ?: false)
            val userResponse = response?.fold(
                { _ -> null },
                { uResponse -> uResponse }
            )
            assertNotNull(userResponse)
            val data = userResponse?.data
            assertNotNull(data)
            assertEquals(registerUserCommand.username, data?.username)
            assertEquals(registerUserCommand.email, data?.email)
            assertEquals(registerUserCommand.firstname, data?.firstname)
            assertEquals(registerUserCommand.lastname, data?.lastname)
        }

        val registerUserCommand2 = createRegisterUserCommand(username = "test")

        runBlocking {
            val result: Mono<Either<UserStoreException, ApiResponse<UserResponse>>> =
                userRegistrator.registerNewUser(registerUserCommand2)

            val response = result.block()
            assertNotNull(response)
            assertTrue(response?.isLeft() ?: false)
            val error = response?.fold(
                { error -> error },
                { _ -> null }
            )
            assertNotNull(error)
            assertEquals("User with username: ${registerUserCommand2.username} already exists", error?.message)
        }
    }

    @Test
    fun `should not register new user with existing email`() {
        val registerUserCommand = createRegisterUserCommand(email = "test@google.com")

        runBlocking {
            val result: Mono<Either<UserStoreException, ApiResponse<UserResponse>>> =
                userRegistrator.registerNewUser(registerUserCommand)

            val response = result.block()
            assertNotNull(response)
            assertTrue(response?.isRight() ?: false)
            val userResponse = response?.fold(
                { _ -> null },
                { uResponse -> uResponse }
            )
            assertNotNull(userResponse)
            val data = userResponse?.data
            assertNotNull(data)
            assertEquals(registerUserCommand.username, data?.username)
            assertEquals(registerUserCommand.email, data?.email)
            assertEquals(registerUserCommand.firstname, data?.firstname)
            assertEquals(registerUserCommand.lastname, data?.lastname)
        }

        val registerUserCommand2 = createRegisterUserCommand(email = "test@google.com")

        runBlocking {
            val result: Mono<Either<UserStoreException, ApiResponse<UserResponse>>> =
                userRegistrator.registerNewUser(registerUserCommand2)

            val response = result.block()
            assertNotNull(response)
            assertTrue(response?.isLeft() ?: false)
            val error = response?.fold(
                { error -> error },
                { _ -> null }
            )
            assertNotNull(error)
            assertEquals("User with email: ${registerUserCommand2.email} already exists", error?.message)
        }
    }

    @Test
    fun `should produce user registered event`() {
        val registerUserCommand = createRegisterUserCommand()

        runBlocking {
            val result: Mono<Either<UserStoreException, ApiResponse<UserResponse>>> =
                userRegistrator.registerNewUser(registerUserCommand)

            val response = result.block()
            assertNotNull(response)
            assertTrue(response?.isRight() ?: false)
            val userResponse = response?.fold(
                { _ -> null },
                { uResponse -> uResponse }
            )
            assertNotNull(userResponse)
            val data = userResponse?.data
            assertNotNull(data)
            assertEquals(registerUserCommand.username, data?.username)
            assertEquals(registerUserCommand.email, data?.email)
            assertEquals(registerUserCommand.firstname, data?.firstname)
            assertEquals(registerUserCommand.lastname, data?.lastname)
        }

        val event = eventPublisher.getEvents().first()
        assertNotNull(event)
        assertEquals(UserCreatedEvent::class.java, event::class.java)
        assertEquals(registerUserCommand.username, event.username)
        assertEquals(registerUserCommand.email, event.email)
        assertEquals(registerUserCommand.firstname, event.firstname)
        assertEquals(registerUserCommand.lastname, event.lastname)
    }

    private fun createRegisterUserCommand(
        username: String = faker.name().username(),
        email: String = faker.internet().emailAddress(),
        password: String = Credential.generateRandomCredentialPassword(),
        firstname: String = faker.name().firstName(),
        lastname: String = faker.name().lastName()
    ) = RegisterUserCommand(
        username = username,
        email = email,
        password = password,
        firstname = firstname,
        lastname = lastname
    )
}
