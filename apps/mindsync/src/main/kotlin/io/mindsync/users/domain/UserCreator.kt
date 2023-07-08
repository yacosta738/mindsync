package io.mindsync.users.domain

import arrow.core.Either
import io.mindsync.users.domain.exceptions.UserStoreException
import reactor.core.publisher.Mono

/**
 * Represents a UserCreator that is responsible for creating a user.
 * @author Yuniel Acosta (acosta)
 * @created 8/7/23
 * @param User the type of user that will be created.
 */
fun interface UserCreator<User> {
    /**
     * Creates a user in the user store.
     *
     * @param user the user object to be created
     * @return a Mono that emits an Either object, where Left is a UserStoreException if there was an error
     *         while creating the user, and Right is the created User object on success
     * @see UserStoreException for more information about the possible errors
     * @see User for more information about the user object
     * @see Mono for more information about the Mono object
     * @see Either for more information about the Either object
     */
    suspend fun create(user: User): Mono<Either<UserStoreException, User>>
}
