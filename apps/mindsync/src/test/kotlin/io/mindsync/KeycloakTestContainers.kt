package io.mindsync

import dasniko.testcontainers.keycloak.KeycloakContainer
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Testcontainers

@Suppress("unused")
@Testcontainers
object KeycloakTestContainers {

    private val keycloak: KeycloakContainer =
        KeycloakContainer().withRealmImportFile("keycloak/demo-realm-test.json")

    @DynamicPropertySource
    fun registerResourceServerIssuerProperty(registry: DynamicPropertyRegistry) {
        if (!keycloak.isRunning) {
            keycloak.start()
        }
        registry.add(
            "spring.security.oauth2.resourceserver.jwt.issuer-uri"
        ) { keycloak.authServerUrl }
    }
}
