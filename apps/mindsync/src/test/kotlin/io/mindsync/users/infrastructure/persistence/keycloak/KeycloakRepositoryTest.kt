package io.mindsync.users.infrastructure.persistence.keycloak

import arrow.core.Either
import io.kotest.common.runBlocking
import io.mindsync.KeycloakTestContainers
import io.mindsync.authentication.domain.Username
import io.mindsync.users.domain.Email
import io.mindsync.users.domain.User
import io.mindsync.users.domain.UserCreator
import io.mindsync.users.domain.exceptions.UserStoreException
import net.datafaker.Faker
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Mono

/**
 *
 *
 * @created 4/7/23
 */
class KeycloakRepositoryTest : KeycloakTestContainers() {

    @Autowired
    private lateinit var userCreator: UserCreator

    private val faker = Faker()

    @Test
    fun `should create a new user`() {
        val user = User.create(
            email = faker.internet().emailAddress(),
            username = faker.name().username(),
            firstName = faker.name().firstName(),
            lastName = faker.name().lastName()
        )

        runBlocking {
            val result: Mono<Either<UserStoreException, User>> = userCreator.create(user)

            val either = result.block()
            assertTrue(either?.isRight() ?: false)
            assertEquals(user.email.value, either?.getOrNull()?.email?.value)
            assertEquals(user.username.value, either?.getOrNull()?.username?.value)
            assertEquals(user.name?.firstName?.value, either?.getOrNull()?.name?.firstName?.value)
            assertEquals(user.name?.lastName?.value, either?.getOrNull()?.name?.lastName?.value)
        }
    }

    @Test
    fun `should not create a new user with an existing email`() {
        val user = User.create(
            email = faker.internet().emailAddress(),
            username = faker.name().username(),
            firstName = faker.name().firstName(),
            lastName = faker.name().lastName()
        )

        runBlocking {
            val result: Mono<Either<UserStoreException, User>> = userCreator.create(user)
            val either = result.block()
            assertTrue(either?.isRight() ?: false)
        }

        runBlocking {
            val result: Mono<Either<UserStoreException, User>> =
                userCreator.create(user.copy(username = Username(faker.name().username())))
            val either = result.block()
            assertTrue(either?.isLeft() ?: false)
        }
    }

    @Test
    fun `should not create a new user with an existing username`() {
        val user = User.create(
            email = faker.internet().emailAddress(),
            username = faker.name().username(),
            firstName = faker.name().firstName(),
            lastName = faker.name().lastName()
        )

        runBlocking {
            val result: Mono<Either<UserStoreException, User>> = userCreator.create(user)
            val either = result.block()
            assertTrue(either?.isRight() ?: false)
        }

        runBlocking {
            val result: Mono<Either<UserStoreException, User>> =
                userCreator.create(user.copy(email = Email(faker.internet().emailAddress())))
            val either = result.block()
            assertTrue(either?.isLeft() ?: false)
        }
    }

    @Test
    fun `should not create a new user with an existing email and username`() {
        val user = User.create(
            email = faker.internet().emailAddress(),
            username = faker.name().username(),
            firstName = faker.name().firstName(),
            lastName = faker.name().lastName()
        )

        runBlocking {
            val result: Mono<Either<UserStoreException, User>> = userCreator.create(user)
            val either = result.block()
            assertTrue(either?.isRight() ?: false)
        }

        runBlocking {
            val result: Mono<Either<UserStoreException, User>> =
                userCreator.create(user.copy(email = Email(faker.internet().emailAddress())))
            val either = result.block()
            assertTrue(either?.isLeft() ?: false)
        }

        runBlocking {
            val result: Mono<Either<UserStoreException, User>> =
                userCreator.create(user.copy(username = Username(faker.name().username())))
            val either = result.block()
            assertTrue(either?.isLeft() ?: false)
        }
    }
}
