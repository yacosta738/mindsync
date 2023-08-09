package io.mindsync.users.infrastructure.persistence.keycloak

import io.kotest.common.runBlocking
import io.mindsync.authentication.domain.Username
import io.mindsync.testcontainers.InfrastructureTestContainers
import io.mindsync.users.domain.Email
import io.mindsync.users.domain.User
import io.mindsync.users.domain.UserCreator
import io.mindsync.users.domain.exceptions.UserStoreException
import kotlinx.coroutines.reactor.awaitSingle
import net.datafaker.Faker
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Mono

class KeycloakRepositoryIntegrationTest : InfrastructureTestContainers() {

    @Autowired
    private lateinit var userCreator: UserCreator

    private val faker = Faker()

    @Test
    fun `should create a new user`() = runBlocking {
        val user = User.create(
            email = faker.internet().emailAddress(),
            firstName = faker.name().firstName(),
            lastName = faker.name().lastName()
        )

        val result: Mono<User> = userCreator.create(user)

        val createdUser = result.block()
        assertNotNull(createdUser)
        assertEquals(createdUser?.email, user.email)
        assertEquals(createdUser?.username, user.username)
        assertEquals(createdUser?.name?.firstName, user.name?.firstName ?: "")
        assertEquals(createdUser?.name?.lastName, user.name?.lastName ?: "")
    }

    @Test
    fun `should not create a new user with an existing email`(): Unit = runBlocking {
        val user = User.create(
            email = faker.internet().emailAddress(),
            firstName = faker.name().firstName(),
            lastName = faker.name().lastName()
        )

        val result: Mono<User> = userCreator.create(user)
        val createdUser = result.block()
        assertNotNull(createdUser)
        assertEquals(createdUser?.email, user.email)
        assertEquals(createdUser?.username, user.username)
        assertEquals(createdUser?.name?.firstName, user.name?.firstName ?: "")
        assertEquals(createdUser?.name?.lastName, user.name?.lastName ?: "")

        val newUsername = Username(faker.name().username())
        val duplicateUser = user.copy(username = newUsername)

        // Act & Assert (try to create duplicate user)
        assertThrows<UserStoreException> {
            userCreator.create(duplicateUser).awaitSingle()
        }.also { exception ->
            assertEquals(
                "User with email: ${user.email.value} or username: ${newUsername.value} already exists.",
                exception.message
            )
        }
    }

    @Test
    fun `should not create a new user with an existing username`(): Unit = runBlocking {
        val user = User.create(
            email = faker.internet().emailAddress(),
            firstName = faker.name().firstName(),
            lastName = faker.name().lastName()
        )

        val result: Mono<User> = userCreator.create(user)
        val createdUser = result.block()
        assertNotNull(createdUser)
        assertEquals(createdUser?.email, user.email)
        assertEquals(createdUser?.username, user.username)
        assertEquals(createdUser?.name?.firstName, user.name?.firstName ?: "")
        assertEquals(createdUser?.name?.lastName, user.name?.lastName ?: "")

        val newEmail = Email("newuser@test.com")
        val duplicateUser = user.copy(email = newEmail)

        // Act & Assert (try to create duplicate user)
        assertThrows<UserStoreException> {
            userCreator.create(duplicateUser).awaitSingle()
        }.also { exception ->
            assertEquals(
                "User with email: ${newEmail.value} or username: ${user.username.value} already exists.",
                exception.message
            )
        }
    }

    @Test
    fun `should not create a new user with an existing email and username`(): Unit = runBlocking {
        val user = User.create(
            email = faker.internet().emailAddress(),
            firstName = faker.name().firstName(),
            lastName = faker.name().lastName()
        )

        val result: Mono<User> = userCreator.create(user)
        val createdUser = result.block()
        assertNotNull(createdUser)
        assertEquals(createdUser?.email, user.email)
        assertEquals(createdUser?.username, user.username)
        assertEquals(createdUser?.name?.firstName, user.name?.firstName ?: "")
        assertEquals(createdUser?.name?.lastName, user.name?.lastName ?: "")

        val newUsername = Username(faker.name().username())
        val newEmail = Email("ultra@gmail.com")
        var duplicateUser = user.copy(username = newUsername)

        // Act & Assert (try to create duplicate user)
        assertThrows<UserStoreException> {
            userCreator.create(duplicateUser).awaitSingle()
        }.also { exception ->
            assertEquals(
                "User with email: ${user.email.value} or username: ${newUsername.value} already exists.",
                exception.message
            )
        }

        duplicateUser = user.copy(email = newEmail)

        // Act & Assert (try to create duplicate user)
        assertThrows<UserStoreException> {
            userCreator.create(duplicateUser).awaitSingle()
        }.also { exception ->
            assertEquals(
                "User with email: ${newEmail.value} or username: ${user.username.value} already exists.",
                exception.message
            )
        }
    }
}
