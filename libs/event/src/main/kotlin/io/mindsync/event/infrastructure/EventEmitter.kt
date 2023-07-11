package io.mindsync.event.infrastructure

import io.mindsync.event.domain.EventConsumer
import io.mindsync.event.domain.EventFilter
import io.mindsync.event.domain.EventMultiplexer
import io.mindsync.event.domain.EventPublisher
import org.springframework.stereotype.Component

/**
 * Represents an event emitter that allows subscribing to events and publishing events of a specific type.
 *
 * @param E the type of events emitted by this event emitter.
 * @constructor Creates a new event emitter that allows subscribing to events and publishing events of a specific type.
 * @property eventMultiplexer the event multiplexer that handles the event subscriptions.
 * @see EventPublisher for publishing events.
 * @see EventConsumer for consuming events.
 * @see EventFilter for filtering events.
 * @see EventMultiplexer for handling event subscriptions.
 * @see EventEmitter for emitting events.
 * @author Yuniel Acosta
 */
@Suppress("UNCHECKED_CAST")
@Component
class EventEmitter<E : Any> : EventPublisher<E> {
    private val eventMultiplexer = EventMultiplexer<E>()

    /**
     * Registers an event consumer with the given event filter.
     *
     * @param filter The event filter to apply.
     * @param consumer The event consumer to register.
     */
    fun on(filter: EventFilter<E>, consumer: EventConsumer<*>) {
        eventMultiplexer.on(filter, consumer as EventConsumer<E>)
    }

    /**
     * Publishes an event to the event multiplexer.
     *
     * @param event the event to be published
     */
    override suspend fun publish(event: E) {
        eventMultiplexer.consume(event)
    }
}
