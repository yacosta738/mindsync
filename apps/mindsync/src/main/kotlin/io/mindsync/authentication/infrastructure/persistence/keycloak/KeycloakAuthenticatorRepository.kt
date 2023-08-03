package io.mindsync.authentication.infrastructure.persistence.keycloak

import io.mindsync.authentication.domain.AccessToken
import io.mindsync.authentication.domain.UserAuthenticator
import io.mindsync.authentication.domain.Username
import io.mindsync.authentication.infrastructure.ApplicationSecurityProperties
import io.mindsync.users.domain.Credential
import io.mindsync.users.domain.exceptions.UserAuthenticationException
import jakarta.ws.rs.BadRequestException
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.representations.AccessTokenResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

/**
 * This class represents a repository for authenticating users using Keycloak. It implements
 * the [UserAuthenticator] interface, which defines the authenticate() method for user authentication.
 *
 * @property applicationSecurityProperties The properties for configuring the Keycloak authentication.
 * @created 31/7/23
 */
@Repository
class KeycloakAuthenticatorRepository(
    private val applicationSecurityProperties: ApplicationSecurityProperties
) : UserAuthenticator {

    /**
     * Creates a new [KeycloakBuilder] instance with password credentials.
     *
     * @param username The username for authentication.
     * @param password The password for authentication.
     * @return The [KeycloakBuilder] instance configured with the provided username and password.
     */
    private fun newKeycloakBuilderWithPasswordCredentials(username: String, password: String): KeycloakBuilder {
        return KeycloakBuilder.builder()
            .realm(applicationSecurityProperties.oauth2.realm)
            .serverUrl(applicationSecurityProperties.oauth2.serverUrl)
            .clientId(applicationSecurityProperties.oauth2.clientId)
            .clientSecret(applicationSecurityProperties.oauth2.clientSecret)
            .username(username)
            .password(password)
    }

    /**
     * Login a user with the given username and password.
     *
     * @param username the username of the user to be logged in
     * @param password the password of the user to be logged in
     * @return The [AccessToken] object containing the access token and other information.
     */
    override suspend fun authenticate(username: Username, password: Credential): AccessToken {
        log.info("Authenticating user with username: {}", username)
        val keycloak = newKeycloakBuilderWithPasswordCredentials(username.value, password.value).build()
        return try {
            val accessTokenResponse = keycloak.tokenManager().getAccessToken()
            accessTokenResponse.toAccessToken()
        } catch (ex: BadRequestException) {
            log.warn("invalid account. User probably hasn't verified email.", ex)
            throw UserAuthenticationException("Invalid account. User probably hasn't verified email.", ex)
        }
    }

    /**
     * Converts an [AccessTokenResponse] object to an AccessToken object.
     *
     * @return The converted [AccessToken] object.
     */
    private fun AccessTokenResponse.toAccessToken(): AccessToken = AccessToken(
        token = this.token,
        expiresIn = this.expiresIn,
        refreshToken = this.refreshToken,
        refreshExpiresIn = this.refreshExpiresIn,
        tokenType = this.tokenType,
        notBeforePolicy = this.notBeforePolicy,
        sessionState = this.sessionState,
        scope = this.scope
    )

    companion object {
        private val log = LoggerFactory.getLogger(KeycloakAuthenticatorRepository::class.java)
    }
}
