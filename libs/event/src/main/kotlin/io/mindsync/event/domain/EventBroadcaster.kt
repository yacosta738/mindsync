package io.mindsync.event.domain

/**
 * Class representing an EventBroadcaster.
 *
 * @param E the type of event being broadcasted
 *
 */
class EventBroadcaster<E : Any> : EventPublisher<E> {
    private val eventPublishers = mutableListOf<EventPublisher<E>>()

    /**
     * Adds an EventPublisher to the list of event publishers.
     *
     * @param eventPublisher The EventPublisher to be added.
     */
    fun use(eventPublisher: EventPublisher<E>) {
        eventPublishers.add(eventPublisher)
    }

    /**
     * Publishes the given event to all event publishers.
     *
     * @param event the event to be published
     */
    override suspend fun publish(event: E) {
        eventPublishers.forEach {
            it.publish(event)
        }
    }
}
