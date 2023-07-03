package io.mindsync.users.infrastructure.persistence.keycloak

import io.mindsync.authentication.infrastructure.ApplicationSecurityProperties
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl
import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private const val CONNECTION_POOL_SIZE = 10

/**
 *
 * @author acosta
 * @created 1/7/23
 */
@Configuration
@EnableConfigurationProperties(ApplicationSecurityProperties::class)
class KeycloakConfiguration {

    @Bean
    fun keycloak(applicationSecurityProperties: ApplicationSecurityProperties): Keycloak =
        KeycloakBuilder.builder()
            .serverUrl(applicationSecurityProperties.oauth2.serverUrl)
            .realm(applicationSecurityProperties.oauth2.adminRealm)
            .grantType(OAuth2Constants.PASSWORD)
            .username(applicationSecurityProperties.oauth2.adminUsername)
            .password(applicationSecurityProperties.oauth2.adminPassword)
            .clientId(applicationSecurityProperties.oauth2.adminClientId)
            .resteasyClient(
                ResteasyClientBuilderImpl()
                    .connectionPoolSize(CONNECTION_POOL_SIZE).build()
            )
            .build()
}
