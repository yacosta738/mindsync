package io.mindsync.users.application

import io.mindsync.common.domain.Service
import io.mindsync.common.domain.error.BusinessRuleValidationException
import io.mindsync.event.domain.EventBroadcaster
import io.mindsync.event.domain.EventPublisher
import io.mindsync.users.application.command.RegisterUserCommand
import io.mindsync.users.application.response.UserResponse
import io.mindsync.users.domain.ApiDataResponse
import io.mindsync.users.domain.User
import io.mindsync.users.domain.UserCreator
import io.mindsync.users.domain.event.UserCreatedEvent
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono

@Service
class UserRegistrator(
    private val userCreator: UserCreator,
    eventPublisher: EventPublisher<UserCreatedEvent>
) {
    private val eventPublisher = EventBroadcaster<UserCreatedEvent>()

    init {
        this.eventPublisher.use(eventPublisher)
    }

    suspend fun registerNewUser(registerUserCommand: RegisterUserCommand): Mono<ApiDataResponse<UserResponse>> {
        log.info(
            "Registering new user with email: {}",
            registerUserCommand.email
        )
        return try {
            val user = registerUserCommand.toUser()
            return userCreator.create(user)
                .map { createdUser ->
                    runBlocking {
                        publishUserCreatedEvent(createdUser)
                    }
                    val userResponse = UserResponse(
                        createdUser.username.value,
                        createdUser.email.value,
                        createdUser.name?.firstName?.value,
                        createdUser.name?.lastName?.value
                    )

                    ApiDataResponse.success(userResponse)
                }
                .onErrorResume { throwable ->
                    log.error("Failed to register new user", throwable)
                    Mono.just(ApiDataResponse.failure("Failed to register new user. Please try again."))
                }
        } catch (e: BusinessRuleValidationException) {
            log.error("Failed to register new user", e)
            Mono.just(ApiDataResponse.failure(e.message))
        }
    }

    private suspend fun publishUserCreatedEvent(user: User) {
        eventPublisher.publish(
            UserCreatedEvent(
                userId = user.id.value.toString(),
                email = user.email.value,
                username = user.username.value,
                firstname = user.name?.firstName?.value,
                lastname = user.name?.lastName?.value
            )
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserRegistrator::class.java)
    }
}
