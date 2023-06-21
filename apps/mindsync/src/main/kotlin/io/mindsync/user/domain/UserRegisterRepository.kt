package io.mindsync.user.domain

import reactor.core.publisher.Mono

interface UserRegisterRepository {
    fun register(user: User): Mono<User>
}
