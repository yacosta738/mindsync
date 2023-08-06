package io.mindsync.error

import io.mindsync.users.domain.exceptions.UserAuthenticationException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler
import java.net.URI
import java.time.Instant

/**
 * This class provides a global exception handling mechanism for the application.
 *
 * It extends the [ResponseEntityExceptionHandler] class to handle exceptions and return appropriate responses.
 *
 * @created 4/8/23
 */
@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {
    /**
     * Handles the [UserAuthenticationException] by creating a ProblemDetail object with the appropriate status, detail and properties.
     *
     * @param e The UserAuthenticationException that was thrown.
     * @return The ProblemDetail object representing the exception.
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UserAuthenticationException::class)
    fun handleUserAuthenticationException(e: UserAuthenticationException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.message)
        problemDetail.title = "User authentication failed"
        problemDetail.setType(URI.create("https://mindsync.io/errors/user-authentication-failed"))
        problemDetail.setProperty("errorCategory", "AUTHENTICATION")
        problemDetail.setProperty("timestamp", Instant.now())
        return problemDetail
    }
}
