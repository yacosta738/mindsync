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
 * The `UserRegisterController` class is responsible for handling user registration requests.
 *
 * This class contains a method to register a user, which takes a `registerUserRequest` object
 * as input and returns a `Mono<ResponseEntity<Response<UserResponse>>>`.
 *
 * The class also includes private helper methods to map the registration result and handle any
 * registration errors.
 * @author Yuniel Acosta
 * @created 30/6/23
 * @constructor Creates a new instance of the UserRegisterController class.
 * @param userRegistrator The user registrator component used to register new users.
 * @see UserRegistrator for more information about the user registrator component.
 * @see RegisterUserRequest for more information about the register user request.
 * @see UserResponse for more information about the user response.
 */
@RestController
class UserRegisterController(private val userRegistrator: UserRegistrator) {

    /**
     * Registers a new user. This method takes a `registerUserRequest` object as input and returns a
     * `Mono<ResponseEntity<Response<UserResponse>>>`.
     * If the user is registered successfully, a `ResponseEntity` containing the user details is returned.
     * If an error occurs, a `ResponseEntity` containing the error message is returned.
     *
     * @param registerUserRequest The request object containing user information.
     * @return A Mono of ResponseEntity containing the response object with user information.
     * If an error occurs, a Mono of ResponseEntity containing the error message is returned.
     * @see RegisterUserRequest for more information about the register user request.
     * @see UserResponse for more information about the user response.
     * @see ResponseEntity for more information about the response entity.
     * @see Mono for more information about the mono object.
     * @see Response for more information about the response object.
     * @see HttpStatus for more information about the http status.
     * @see Either for more information about the either object.
     * @see UserStoreException for more information about the user store exception.
     */
    @PostMapping("/api/register")
    suspend fun registerUser(@RequestBody @Validated registerUserRequest: RegisterUserRequest):
        Mono<ResponseEntity<Response<UserResponse>>> {
        log.info("Registering new user with email: {}", registerUserRequest.email)

        return userRegistrator.registerNewUser(registerUserRequest.toRegisterUserCommand())
            .flatMap(::mapRegistrationResult)
            .onErrorResume(::handleRegistrationError)
    }

    /**
     * Maps the registration result to a Mono ResponseEntity.
     *
     * @param result the registration result as an Either object, where the left side represents an error
     * and the right side represents a successful response
     * @return a Mono ResponseEntity containing the mapped result
     */
    private fun mapRegistrationResult(result: Either<UserStoreException, Response<UserResponse>>):
        Mono<ResponseEntity<Response<UserResponse>>> {
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

    /**
     * Handles registration error and returns a Mono wrapping a ResponseEntity containing a
     * [Response] object with a [UserResponse].
     *
     * @param error the Throwable representing the registration error
     * @return a Mono wrapping a ResponseEntity with HTTP status code 500 (Internal Server Error)
     */
    private fun handleRegistrationError(error: Throwable): Mono<ResponseEntity<Response<UserResponse>>> {
        log.error("Error during user registration: {}", error.message)
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserRegisterController::class.java)
    }
}
