package io.mindsync.users.infrastructure.event.consumer

import io.mindsync.event.domain.EventConsumer
import io.mindsync.event.domain.Subscribe
import io.mindsync.users.domain.event.UserCreatedEvent
import io.mindsync.users.infrastructure.persistence.keycloak.KeycloakRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 *
 * @author Yuniel Acosta (acosta)
 * @created 7/7/23
 */
@Component
@Subscribe(filterBy = UserCreatedEvent::class)
class SendEmailVerification(private val keycloakRepository: KeycloakRepository) : EventConsumer<UserCreatedEvent> {
    override suspend fun consume(event: UserCreatedEvent) {
        log.info("Sending email verification to user with id: {}", event.userId)
        keycloakRepository.verify(event.userId)
    }
    companion object {
        private val log = LoggerFactory.getLogger(SendEmailVerification::class.java)
    }
}
