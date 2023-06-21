package io.mindsync

import dasniko.testcontainers.keycloak.KeycloakContainer
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
open class KeycloakTestContainers {
    init {
        keycloak.start()
    }
    companion object{
        private val keycloak: KeycloakContainer = KeycloakContainer().withRealmImportFile("keycloak/demo-realm-test.json")
        @DynamicPropertySource
        fun registerResourceServerIssuerProperty(registry: DynamicPropertyRegistry) {
            registry.add(
                "adapter.keycloak.base-url"
            ) { keycloak.authServerUrl }

            registry.add(
                "adapter.keycloak.issuer-url"
            ) { "${keycloak.authServerUrl}/realms/demo" }

            registry.add(
                "adapter.keycloak.client-id"
            ) { "spring-security" }

            registry.add(
                "adapter.keycloak.client-secret"
            ) { "342a09be-808a-4b65-90d0-a79f2de45048" }

            registry.add(
                "adapter.keycloak.realm"
            ) { "demo" }

            registry.add(
                "adapter.keycloak.admin-client-id"
            ) { "admin-cli" }

            registry.add(
                "adapter.keycloak.admin-username"
            ) { "admin" }

            registry.add(
                "adapter.keycloak.admin-password"
            ) { "admin" }

            registry.add(
                "adapter.keycloak.admin-realm"
            ) { "master" }

        }
    }
}
