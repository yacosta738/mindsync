package io.mindsync.event.domain

fun interface EventConsumer<E : Any> {
    suspend fun consume(event: E)
}
