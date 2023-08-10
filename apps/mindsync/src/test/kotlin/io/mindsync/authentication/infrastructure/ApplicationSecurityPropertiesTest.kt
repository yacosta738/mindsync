package io.mindsync.authentication.infrastructure

import io.mindsync.UnitTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
@UnitTest
internal class ApplicationSecurityPropertiesTest {
    @Test
    fun shouldGetDefaultProperties() {
        val properties = ApplicationSecurityProperties()
        assertThat(properties.contentSecurityPolicy).isEqualTo(
            DEFAULT_CONTENT_SECURITY_POLICY
        )
        assertThat(properties.oauth2.audience).isEmpty()
    }

    @Test
    fun shouldUpdatedConfiguration() {
        val properties = ApplicationSecurityProperties()
        properties.contentSecurityPolicy = "policy"
        properties.oauth2.audience = listOf("audience").toMutableList()
        assertThat(properties.contentSecurityPolicy).isEqualTo("policy")
        assertThat(properties.oauth2.audience).containsExactly("audience")
    }

    companion object {
        @Suppress("MaxLineLength")
        private const val DEFAULT_CONTENT_SECURITY_POLICY =
            "default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:"
    }
}
