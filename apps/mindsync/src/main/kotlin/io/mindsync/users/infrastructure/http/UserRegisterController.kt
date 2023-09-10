package io.mindsync.users.infrastructure.http

import io.mindsync.users.application.UserRegistrator
import io.mindsync.users.application.response.UserResponse
import io.mindsync.users.domain.ApiDataResponse
import io.mindsync.users.domain.ApiResponseStatus
import io.mindsync.users.domain.exceptions.UserStoreException
import io.mindsync.users.infrastructure.http.request.RegisterUserRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
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
 *
 * @constructor Creates a new instance of the UserRegisterController class.
 * @param userRegistrator The user registrator component used to register new users.
 * @see UserRegistrator for more information about the user registrator component.
 * @see RegisterUserRequest for more information about the register user request.
 * @see UserResponse for more information about the user response.
 * @created 30/6/23
 */
@RestController
@RequestMapping("/api", produces = ["application/vnd.api.v1+json"])
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
     * @see ApiDataResponse for more information about the response object.
     * @see HttpStatus for more information about the http status.
     * @see UserStoreException for more information about the user store exception.
     */
    @Operation(summary = "Register endpoint")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Created"),
        ApiResponse(responseCode = "400", description = "Bad request"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    )
    @PostMapping("/register")
    suspend fun registerUser(@Validated @RequestBody registerUserRequest: RegisterUserRequest):
        Mono<ResponseEntity<ApiDataResponse<UserResponse>>> {
        log.info("Registering new user with email: {}", registerUserRequest.email)
        return userRegistrator.registerNewUser(registerUserRequest.toRegisterUserCommand())
            .flatMap(::mapRegistrationResult)
            .onErrorResume(::handleRegistrationError)
    }

    private fun mapRegistrationResult(apiDataResponse: ApiDataResponse<UserResponse>):
        Mono<ResponseEntity<ApiDataResponse<UserResponse>>> {
        return when (apiDataResponse.status) {
            ApiResponseStatus.SUCCESS -> Mono.just(ResponseEntity.status(HttpStatus.CREATED).body(apiDataResponse))
            ApiResponseStatus.FAILURE -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiDataResponse))
        }
    }

    /**
     * Handles registration error and returns a Mono wrapping a ResponseEntity containing a
     * [ApiDataResponse] object with a [UserResponse].
     *
     * @param error the Throwable representing the registration error
     * @return a Mono wrapping a ResponseEntity with HTTP status code 500 (Internal Server Error)
     */
    private fun handleRegistrationError(error: Throwable): Mono<ResponseEntity<ApiDataResponse<UserResponse>>> {
        log.error("Error during user registration: {}", error.message)
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserRegisterController::class.java)
    }
}
