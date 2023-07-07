package io.mindsync

import dasniko.testcontainers.keycloak.KeycloakContainer
import org.junit.jupiter.api.BeforeAll
import org.slf4j.LoggerFactory
import org.springframework.boot.json.JacksonJsonParser
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.testcontainers.junit.jupiter.Testcontainers
import java.net.URI
import java.net.URISyntaxException

@Suppress("unused")
@Testcontainers
@IntegrationTest
abstract class KeycloakTestContainers {
    init {
        startKeycloak()
    }

    protected fun getAdminBearer(): String? {
        try {
            val authServerUrl = removeLastSlash(keycloakContainer.authServerUrl)
            val openIdConnectToken = "protocol/openid-connect/token"
            val authorizationURI = URI("$authServerUrl/realms/$REALM/$openIdConnectToken")
            val webclient = WebClient.builder().build()
            val formData: MultiValueMap<String, String> = LinkedMultiValueMap()
            formData.add("grant_type", "password")
            formData.add("client_id", CLIENT_ID)
            formData.add("username", "jane.doe@mindsync.com")
            val password = "s3cr3t"
            formData.add("password", password)

            val result = webclient.post()
                .uri(authorizationURI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(String::class.java)
                .block()

            val jsonParser = JacksonJsonParser()

            return "Bearer " + jsonParser.parseMap(result)["access_token"]
                .toString()
        } catch (e: URISyntaxException) {
            log.error("Can't obtain an access token from Keycloak!", e)
        }

        return null
    }

    companion object {
        private val log = LoggerFactory.getLogger(KeycloakTestContainers::class.java)
        private const val ADMIN_USER: String = "admin"
        private const val ADMIN_PASSWORD: String = "secret"
        private const val REALM: String = "mindsync"
        private const val CLIENT_ID: String = "web_app"
        private const val ADMIN_CLI = "admin-cli"
        private const val ADMIN_REALM = "master"

        @JvmStatic
        private val keycloakContainer: KeycloakContainer =
            KeycloakContainer().withRealmImportFile("keycloak/demo-realm-test.json")
                .withAdminUsername(ADMIN_USER)
                .withAdminPassword(ADMIN_PASSWORD)

        fun getKeycloak(): KeycloakContainer {
            startKeycloak()
            return keycloakContainer
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerResourceServerIssuerProperty(registry: DynamicPropertyRegistry) {
            log.info("Registering Keycloak Properties \uD83D\uDC77\uD83C\uDFFB\u200D♂\uFE0F")
            startKeycloak()
            val authServerUrl = removeLastSlash(keycloakContainer.authServerUrl)
            registry.add(
                "spring.security.oauth2.resourceserver.jwt.issuer-uri"
            ) { authServerUrl }

            registry.add(
                "application.security.oauth2.base-url"
            ) { authServerUrl }

            registry.add(
                "application.security.oauth2.server-url"
            ) { authServerUrl }

            registry.add(
                "application.security.oauth2.issuer-uri"
            ) { "$authServerUrl/realms/$REALM" }

            registry.add(
                "application.security.oauth2.realm"
            ) { REALM }

            registry.add(
                "application.security.oauth2.client-id"
            ) { CLIENT_ID }

            registry.add(
                "application.security.oauth2.admin-client-id"
            ) { ADMIN_CLI }

            registry.add(
                "application.security.oauth2.admin-realm"
            ) { ADMIN_REALM }

            registry.add(
                "application.security.oauth2.admin-username"
            ) { ADMIN_USER }

            registry.add(
                "application.security.oauth2.admin-password"
            ) { ADMIN_PASSWORD }
        }

        private fun removeLastSlash(url: String): String {
            if (url.length > 1 && url.endsWith("/")) {
                return url.substring(0, url.length - 1)
            }
            return url
        }

        @BeforeAll
        @JvmStatic
        fun startKeycloak() {
            log.info("Keycloak Containers Start \uD83D\uDE80")
            if (!keycloakContainer.isRunning) {
                keycloakContainer.start()
            }
        }
    }
}
