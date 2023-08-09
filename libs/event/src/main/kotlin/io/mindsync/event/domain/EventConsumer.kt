package io.mindsync.event.domain

/**
 * Functional interface for consuming events of type [E].
 *
 * @param E the type of event to be consumed.
 *
 */
fun interface EventConsumer<E : Any> {
    /**
     * Consume method for handling events.
     *
     * @param event The event to be consumed.
     */
    suspend fun consume(event: E)
}
