package io.mindsync.event.domain

import java.time.LocalDateTime

open class BaseDomainEvent(val occuredOn: LocalDateTime = LocalDateTime.now()) : DomainEvent {
    private var eventVersion = 1

    override fun eventVersion(): Int {
        return eventVersion
    }

    override fun occurredOn(): LocalDateTime {
        return occuredOn
    }
}
