package io.mindsync.event.domain

/**
 * Represents an event filter.
 *
 * An event filter is a functional interface that defines a filter for events of a specific type.
 * The EventFilter interface provides a single method `filter(event: E): Boolean` that accepts an event of type E
 * and returns a boolean value indicating whether the event passes the filter.
 *
 * @param E the type of events that can be filtered
 * @author Yuniel Acosta
 */
fun interface EventFilter<E : Any> {
    /**
     * Filters the given event based on certain conditions.
     *
     * @param event The event to be filtered.
     * @return True if the event passes the filtering conditions, false otherwise.
     */
    suspend fun filter(event: E): Boolean
}
