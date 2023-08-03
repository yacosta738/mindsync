package io.mindsync.users.application

import arrow.core.Either
import io.mindsync.users.domain.User
import io.mindsync.users.domain.UserCreator
import io.mindsync.users.domain.exceptions.UserStoreException
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap

/**
 *
 *
 * @created 3/7/23
 */
class InMemoryUserRepository(
    private val users: MutableMap<String, User> = ConcurrentHashMap()
) : UserCreator {
    override suspend fun create(user: User): Mono<Either<UserStoreException, User>> {
        checkIfUserExist(user).let {
            if (it != null) {
                return Mono.just(Either.Left(UserStoreException(it)))
            }
        }

        users[user.id.value.toString()] = user
        return Mono.just(Either.Right(user))
    }

    private fun checkIfUserExist(user: User): String? {
        var message: String? = null
        if (getUser(user.id.value.toString()) != null) {
            message = "User already exists"
        }
        if (getUserByEmail(user.email.value) != null) {
            message = "User with email: ${user.email.value} already exists"
        }

        if (getUserByUsername(user.username.value) != null) {
            message = "User with username: ${user.username.value} already exists"
        }

        return message
    }

    private fun getUser(userId: String): User? = users[userId]

    private fun getUserByEmail(email: String): User? = users.values.firstOrNull { it.email.value == email }

    private fun getUserByUsername(username: String): User? = users.values.firstOrNull { it.username.value == username }
}
