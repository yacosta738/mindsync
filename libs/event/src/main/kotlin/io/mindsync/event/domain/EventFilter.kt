package io.mindsync.event.domain

fun interface EventFilter<E : Any> {
    suspend fun filter(event: E): Boolean
}
