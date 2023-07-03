package io.mindsync.event.domain

fun interface EventPublisher<E : Any> {
    suspend fun publish(event: E)
}
