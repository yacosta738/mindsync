package io.mindsync.users.application

import arrow.core.Either
import io.mindsync.common.domain.Service
import io.mindsync.common.domain.error.BusinessRuleValidationException
import io.mindsync.event.domain.EventBroadcaster
import io.mindsync.event.domain.EventPublisher
import io.mindsync.users.application.command.RegisterUserCommand
import io.mindsync.users.domain.Response
import io.mindsync.users.domain.User
import io.mindsync.users.domain.UserCreator
import io.mindsync.users.domain.event.UserCreatedEvent
import io.mindsync.users.domain.exceptions.UserStoreException
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

/**
 *
 * @author acosta
 * @created 29/6/23
 */
@Service
class UserRegistrator(
    private val userCreator: UserCreator<User>,
    eventPublisher: EventPublisher<UserCreatedEvent>
) {
    private val eventPublisher = EventBroadcaster<UserCreatedEvent>()

    init {
        this.eventPublisher.use(eventPublisher)
    }

    suspend fun registerNewUser(registerUserCommand: RegisterUserCommand): Mono<Either<UserStoreException, Response<UserResponse>>> {
        log.info(
            "Registering new user with email: {} and username: {}",
            registerUserCommand.email,
            registerUserCommand.username
        )
        return userCreator.create(
            try {
                registerUserCommand.toUser()
            } catch (e: BusinessRuleValidationException) {
                log.error("Error transforming command to user: {}", e.message)
                return Mono.just(Either.Left(UserStoreException(e.message)))
            }
        ).subscribeOn(Schedulers.parallel())
            .awaitSingle()
            .fold(
                { error ->
                    log.error("Error saving user: {}", error.message)
                    Mono.just(Either.Left(error))
                },
                { user ->
                    log.info("User saved successfully with id: {}", user.id)
                    eventPublisher.publish(
                        UserCreatedEvent(
                            userId = user.id.value.toString(),
                            email = user.email.value,
                            username = user.username.value,
                            firstname = user.name?.firstName?.value,
                            lastname = user.name?.lastName?.value
                        )
                    )
                    Mono.just(
                        Either.Right(
                            Response.success(
                                UserResponse(
                                    user.username.value,
                                    user.email.value,
                                    user.name?.firstName?.value,
                                    user.name?.lastName?.value
                                )
                            )
                        )
                    )
                }
            )
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserRegistrator::class.java)
    }
}
