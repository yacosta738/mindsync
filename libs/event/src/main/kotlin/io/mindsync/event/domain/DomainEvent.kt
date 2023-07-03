package io.mindsync.event.domain

import java.time.LocalDateTime

interface DomainEvent {
    fun eventVersion(): Int
    fun occurredOn(): LocalDateTime?
}
