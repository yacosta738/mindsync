package io.mindsync.users.domain

import arrow.core.Either
import io.mindsync.users.domain.exceptions.UserNotFoundException
import reactor.core.publisher.Mono

/**
 *
 * @author Yuniel Acosta (acosta)
 * @created 2/7/23
 */
interface UserFinder<User, UserId> {
    suspend fun findById(id: UserId): Mono<Either<UserNotFoundException, User>>
    suspend fun findByEmail(anEmail: String): Mono<Either<UserNotFoundException, User>>
}
