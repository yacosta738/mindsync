package io.mindsync.users.infrastructure.http

import arrow.core.Either
import io.mindsync.users.application.UserRegistrator
import io.mindsync.users.application.UserResponse
import io.mindsync.users.domain.ApiError
import io.mindsync.users.domain.Response
import io.mindsync.users.domain.exceptions.UserStoreException
import io.mindsync.users.infrastructure.dto.RegisterUserRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

/**
 *
 * @author acosta
 * @created 30/6/23
 */
@RestController
class UserRegisterController(private val userRegistrator: UserRegistrator) {

    @PostMapping("/api/register")
    suspend fun registerUser(@RequestBody @Validated registerUserRequest: RegisterUserRequest): Mono<ResponseEntity<Response<UserResponse>>> {
        log.info("Registering new user with email: {}", registerUserRequest.email)

        return userRegistrator.registerNewUser(registerUserRequest.toRegisterUserCommand())
            .flatMap(::mapRegistrationResult)
            .onErrorResume(::handleRegistrationError)
    }

    private fun mapRegistrationResult(result: Either<UserStoreException, Response<UserResponse>>): Mono<ResponseEntity<Response<UserResponse>>> {
        return result.fold(
            { error ->
                log.error("Error: {}", error.message)
                val errorMessage = error.message
                Mono.just(ResponseEntity.badRequest().body(Response.error(listOf(ApiError(errorMessage)))))
            },
            { user ->
                log.info("User saved successfully with email: {}", user.data?.email)
                Mono.just(ResponseEntity.ok(user))
            }
        )
    }

    private fun handleRegistrationError(error: Throwable): Mono<ResponseEntity<Response<UserResponse>>> {
        log.error("Error during user registration: {}", error.message)
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserRegisterController::class.java)
    }
}
