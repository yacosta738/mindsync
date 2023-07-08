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
 * The `UserRegistrator` class is responsible for registering new users in the system.
 *
 * @author Yuniel Acosta (acosta)
 * @created 8/7/23
 * @constructor Creates a new instance of the UserRegistrator class.
 * @param userCreator The user creator component used to create user objects.
 * @param eventPublisher The event publisher component used to publish user created events.
 * @see UserCreator for more information about the user creator component.
 * @see EventPublisher for more information about the event publisher component.
 * @see UserCreatedEvent for more information about the user created event.
 * @see User for more information about the user object.
 * @see RegisterUserCommand for more information about the register user command.
 * @see UserStoreException for more information about the user store exception.
 * @see Response for more information about the response object.
 * @see Either for more information about the either object.
 * @see Mono for more information about the mono object.
 * @see EventBroadcaster for more information about the event broadcaster object.
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

    /**
     * Registers a new user with the given user registration command.
     *
     * @param registerUserCommand The user registration command containing the user details.
     * @return A Mono of Either, where the left side represents a UserStoreException and the right side represents a Response containing the user details.
     */
    suspend fun registerNewUser(registerUserCommand: RegisterUserCommand):
        Mono<Either<UserStoreException, Response<UserResponse>>> {
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
