package io.mindsync.authentication.infrastructure.http

import io.mindsync.authentication.application.AuthenticateUserQueryHandler
import io.mindsync.authentication.application.query.AuthenticateUserQuery
import io.mindsync.authentication.domain.AccessToken
import io.mindsync.authentication.infrastructure.http.request.LoginRequest
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * This class is a controller responsible for handling user authentication related requests.
 *
 * @property authenticateUserQueryHandler The query handler used for authenticating users.
 * @created 31/7/23
 */
@RestController
@RequestMapping("/api")
class UserAuthenticatorController(private val authenticateUserQueryHandler: AuthenticateUserQueryHandler) {

    @PostMapping("/login")
    suspend fun login(@Validated @RequestBody loginRequest: LoginRequest): ResponseEntity<AccessToken> {
        val authenticateUserQuery = AuthenticateUserQuery(
            username = loginRequest.username,
            password = loginRequest.password
        )
        val accessToken = authenticateUserQueryHandler.handle(authenticateUserQuery)
        return ResponseEntity.ok(accessToken)
    }
}
