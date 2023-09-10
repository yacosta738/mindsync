package io.mindsync.authentication.infrastructure.persistence.keycloak

import io.mindsync.authentication.domain.AccessToken
import io.mindsync.authentication.domain.RefreshToken
import io.mindsync.authentication.domain.RefreshTokenManager
import io.mindsync.authentication.infrastructure.ApplicationSecurityProperties
import io.mindsync.authentication.infrastructure.mapper.AccessTokenResponseMapper.toAccessToken
import io.mindsync.users.domain.exceptions.UserRefreshTokenException
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.keycloak.representations.AccessTokenResponse
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import java.net.URI

/**
 * Represents a [RefreshTokenManager] that is responsible for refreshing the access token of the user.
 */
@Service
class KeycloakRefreshTokenManagerRepository(
    private val applicationSecurityProperties: ApplicationSecurityProperties
) : RefreshTokenManager {
    private val webClient: WebClient = WebClient.builder().build()
    private val authorizationURI = URI(
        constructOpenIdConnectTokenUrl()
    )

    private fun constructOpenIdConnectTokenUrl(
        realm: String = applicationSecurityProperties.oauth2.realm,
        serverUrl: String = applicationSecurityProperties.oauth2.serverUrl
    ) = "$serverUrl/realms/$realm/$OPENID_CONNECT_TOKEN"

    /**
     * Refreshes the access token of the user.
     *
     * @param refreshToken the refresh token of the user
     * @return the access token of the user
     */
    override suspend fun refresh(refreshToken: RefreshToken): AccessToken {
        log.info("Refreshing access token for user with refresh token")
        val formData: MultiValueMap<String, String> = LinkedMultiValueMap()
        formData.add("grant_type", "refresh_token")
        formData.add("client_id", applicationSecurityProperties.oauth2.clientId)
        formData.add("refresh_token", refreshToken.value)
        return webClient.post()
            .uri(authorizationURI)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            .bodyToMono(AccessTokenResponse::class.java)
            .map { it.toAccessToken() }
            .onErrorMap {
                UserRefreshTokenException("Could not refresh access token", it)
            }
            .awaitSingleOrNull() ?: throw UserRefreshTokenException("Could not refresh access token")
    }

    companion object {
        private const val OPENID_CONNECT_TOKEN = "protocol/openid-connect/token"
        private val log = LoggerFactory.getLogger(KeycloakRefreshTokenManagerRepository::class.java)
    }
}
