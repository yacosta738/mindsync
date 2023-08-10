package io.mindsync.authentication.infrastructure

import io.mindsync.authentication.infrastructure.ApplicationSecurityProperties.Companion.CONTENT_SECURITY_POLICY
import io.mindsync.authentication.infrastructure.ApplicationSecurityProperties.Companion.OAuth2
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated

/**
 * [ApplicationSecurityProperties] is a configuration class that handles security properties for the application.
 *
 * This class is responsible for managing the security-related properties of the application.
 * It is annotated with @Validated to enable validation of the properties.
 * It is annotated with @Configuration to indicate that it is a Spring configuration class.
 * It is annotated with @ConfigurationProperties to specify the prefix for the properties and to enable ignoring
 * unknown fields.
 *
 * Properties:
 * - oauth2: The OAuth2 configuration properties. It is an instance of the inner [OAuth2] class.
 * - contentSecurityPolicy: The Content Security Policy for the application.
 *
 * Inner Class:
 * - [OAuth2]: Configuration properties for OAuth2 authentication.
 *      - baseUrl: The base URL for the OAuth2 server.
 *      - serverUrl: The URL for the OAuth2 server.
 *      - issuerUri: The issuer URI for the OAuth2 server.
 *      - realm: The realm for OAuth2 authentication.
 *      - clientId: The client ID for OAuth2 authentication.
 *      - clientSecret: The client secret for OAuth2 authentication.
 *      - adminClientId: The admin client ID for OAuth2 authentication.
 *      - adminRealm: The admin realm for OAuth2 authentication.
 *      - adminUsername: The admin username for OAuth2 authentication.
 *      - adminPassword: The admin password for OAuth2 authentication.
 *      - audience: The list of audiences for OAuth2 authentication.
 *
 * Constants:
 * - [CONTENT_SECURITY_POLICY]: The default Content Security Policy for the application.
 */
@Validated
@Configuration
@ConfigurationProperties(prefix = "application.security", ignoreUnknownFields = false)
class ApplicationSecurityProperties(
    val oauth2: OAuth2 = OAuth2(),
    var contentSecurityPolicy: String = CONTENT_SECURITY_POLICY
) {
    companion object {
        data class OAuth2(
            var baseUrl: String = "",
            var serverUrl: String = "",
            var issuerUri: String = "",
            var realm: String = "",
            var clientId: String = "",
            var clientSecret: String = "",
            var adminClientId: String = "admin-cli",
            var adminRealm: String = "",
            var adminUsername: String = "",
            var adminPassword: String = "",
            var audience: MutableList<String> = ArrayList()
        )

        @Suppress("MaxLineLength")
        private const val CONTENT_SECURITY_POLICY =
            "default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:"
    }
}
