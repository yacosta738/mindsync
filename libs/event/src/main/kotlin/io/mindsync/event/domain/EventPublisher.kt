package io.mindsync.event.domain

/**
 * Represents an event publisher that is capable of publishing events.
 *
 * @param E the type of event to be published
 * @author Yuniel Acosta
 */
fun interface EventPublisher<E : Any> {
    /**
     * Publishes the given event.
     *
     * @param event The event to be published.
     */
    suspend fun publish(event: E)
}
