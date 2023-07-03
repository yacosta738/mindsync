package io.mindsync.users.domain

import arrow.core.Either
import io.mindsync.users.domain.exceptions.UserStoreException
import reactor.core.publisher.Mono

/**interface UserRepository : UserFinder<User, UserId>, UserCreator<User>*/

fun interface UserCreator<User> {
    suspend fun create(user: User): Mono<Either<UserStoreException, User>>
}
