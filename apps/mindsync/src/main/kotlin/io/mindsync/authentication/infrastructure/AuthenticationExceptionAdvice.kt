package io.mindsync.authentication.infrastructure

import io.mindsync.authentication.domain.error.NotAuthenticatedUserException
import io.mindsync.authentication.domain.error.UnknownAuthenticationException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler


@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE - 20000)
internal class AuthenticationExceptionAdvice {
  @ExceptionHandler(NotAuthenticatedUserException::class)
  fun handleNotAuthenticateUser(ex: NotAuthenticatedUserException): ProblemDetail {
    val detail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED)
    detail.title = "not authenticated"
    detail.setProperty(MESSAGE_KEY, "error.http.401")
    return detail
  }

  @ExceptionHandler(UnknownAuthenticationException::class)
  fun handleUnknownAuthentication(ex: UnknownAuthenticationException): ProblemDetail {
    val detail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    detail.title = "unknown authentication"
    detail.setProperty(MESSAGE_KEY, "error.http.500")
    return detail
  }

  companion object {
    private const val MESSAGE_KEY = "message"
  }
}
