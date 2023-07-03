package io.mindsync.common.domain.repository

import arrow.core.Either
import io.mindsync.common.domain.error.BusinessRuleValidationException
import reactor.core.publisher.Mono

interface BaseRepository<T : Any, ID : Any> {
    suspend fun save(entity: T): Mono<Either<BusinessRuleValidationException, T>>
    suspend fun findById(id: ID): Mono<Either<BusinessRuleValidationException, T>>
    suspend fun findAll(): Mono<Either<BusinessRuleValidationException, Iterable<T>>>
}
