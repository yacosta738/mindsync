package io.mindsync.event.infrastructure

import io.mindsync.event.domain.EventConsumer
import io.mindsync.event.domain.EventFilter
import io.mindsync.event.domain.EventMultiplexer
import io.mindsync.event.domain.EventPublisher
import org.springframework.stereotype.Component

@Suppress("UNCHECKED_CAST")
@Component
class EventEmitter<E : Any> : EventPublisher<E> {
    private val eventMultiplexer = EventMultiplexer<E>()

    fun on(filter: EventFilter<E>, consumer: EventConsumer<*>) {
        eventMultiplexer.on(filter, consumer as EventConsumer<E>)
    }

    override suspend fun publish(event: E) {
        eventMultiplexer.consume(event)
    }
}
