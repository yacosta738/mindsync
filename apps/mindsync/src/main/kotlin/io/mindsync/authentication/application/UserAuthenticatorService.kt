package io.mindsync.authentication.application

import io.mindsync.authentication.domain.AccessToken
import io.mindsync.authentication.domain.UserAuthenticator
import io.mindsync.authentication.domain.Username
import io.mindsync.common.domain.Service
import io.mindsync.users.domain.Credential
import org.slf4j.LoggerFactory

/**
 *
 * @created 31/7/23
 */
@Service
class UserAuthenticatorService(private val userAuthenticator: UserAuthenticator) {
    /**
     * Authenticates a user.
     *
     * @param username the username of the user to be authenticated
     * @param password the password of the user to be authenticated
     */
    suspend fun authenticate(username: Username, password: Credential): AccessToken {
        log.info("Authenticating user with username: {}", username)
        return userAuthenticator.authenticate(username, password)
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserAuthenticatorService::class.java)
    }
}
