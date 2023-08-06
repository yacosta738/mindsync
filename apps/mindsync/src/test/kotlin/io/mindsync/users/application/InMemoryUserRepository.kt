package io.mindsync.users.application

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
    /**
     * Create a new user.
     *
     * @param user The user object to be created.
     * @return A Mono emitting the created User object.
     */
    override suspend fun create(user: User): Mono<User> {
        if (checkIfUserExist(user)) {
            return Mono.error(
                UserStoreException(
                    "User with email: ${user.email.value} or username: ${user.username.value} already exists."
                )
            )
        }
        users[user.id.value.toString()] = user
        return Mono.just(user)
    }

    private fun checkIfUserExist(user: User): Boolean {
        return getUser(user.id.value.toString()) != null ||
            getUserByEmail(user.email.value) != null ||
            getUserByUsername(user.username.value) != null
    }

    private fun getUser(userId: String): User? = users[userId]

    private fun getUserByEmail(email: String): User? = users.values.firstOrNull { it.email.value == email }

    private fun getUserByUsername(username: String): User? = users.values.firstOrNull { it.username.value == username }
}
