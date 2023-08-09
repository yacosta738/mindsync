package io.mindsync.users.domain

import reactor.core.publisher.Mono

/**
 * Represents a UserCreator that is responsible for creating a user.
 *
 * A UserCreator is a functional interface with a single method `create`, which takes a `User` object
 * and asynchronously creates a user. The method returns a `Mono<User>` that represents a stream that emits
 * a single user object.
 * @created 8/7/23
 */
fun interface UserCreator {
    /**
     * Create a new user.
     *
     * @param user The user object to be created.
     * @return A Mono emitting the created User object.
     */
    suspend fun create(user: User): Mono<User>
}
