package io.mindsync.users.domain.event

import io.mindsync.event.domain.BaseDomainEvent

/**
 * User created event is published when a new user is created.
 * @author Yuniel Acosta (acosta)
 * @created 2/7/23
 */
data class UserCreatedEvent(
    val userId: String,
    val email: String,
    val username: String,
    val firstname: String?,
    val lastname: String?
) : BaseDomainEvent()
