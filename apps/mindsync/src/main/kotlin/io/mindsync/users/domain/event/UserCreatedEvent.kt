package io.mindsync.users.domain.event

import io.mindsync.event.domain.BaseDomainEvent
data class UserCreatedEvent(
    val userId: String,
    val email: String,
    val username: String,
    val firstname: String?,
    val lastname: String?
) : BaseDomainEvent()
