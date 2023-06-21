package io.mindsync.authentication.domain.error


sealed class AuthenticationException : RuntimeException()

class NotAuthenticatedUserException : AuthenticationException()

class UnknownAuthenticationException : AuthenticationException()
