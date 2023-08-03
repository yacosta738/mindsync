package io.mindsync.authentication.application

import io.mindsync.authentication.application.query.AuthenticateUserQuery
import io.mindsync.authentication.domain.AccessToken
import io.mindsync.authentication.domain.Username
import io.mindsync.common.domain.Service
import io.mindsync.common.domain.query.QueryHandler
import io.mindsync.users.domain.Credential
import io.mindsync.users.domain.CredentialId
import org.slf4j.LoggerFactory
import java.util.UUID

/**
 * Handles the [AuthenticateUserQuery] query. This query is used to authenticate a user.
 * @created 31/7/23
 */
@Service
class AuthenticateUserQueryHandler(private val authenticator: UserAuthenticatorService) :
    QueryHandler<AuthenticateUserQuery, AccessToken> {

    /**
     * Handles the given query.
     * @param query The query to handle.
     * @return The response of the query.
     */
    override suspend fun handle(query: AuthenticateUserQuery): AccessToken {
        log.info("Authenticating user with username: {}", query.username)
        val username = Username(query.username)
        val password = Credential(CredentialId(UUID.randomUUID()), query.password)
        return authenticator.authenticate(username, password)
    }
    companion object {
        private val log = LoggerFactory.getLogger(AuthenticateUserQueryHandler::class.java)
    }
}
