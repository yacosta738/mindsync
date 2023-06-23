package io.mindsync.authentication.infrastructure

import jakarta.validation.constraints.NotNull
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import java.util.*

@Validated
@Configuration
@ConfigurationProperties(prefix = "application.security", ignoreUnknownFields = false)
class ApplicationSecurityProperties(
    val oauth2: OAuth2 = OAuth2(),
    var contentSecurityPolicy: String = CONTENT_SECURITY_POLICY
) {
    companion object {
        data class OAuth2(private val audience: MutableList<String> = ArrayList()) {
            fun getAudience(): List<String> {
                return Collections.unmodifiableList(audience)
            }

            fun setAudience(audience: @NotNull List<String>) {
                this.audience.addAll(audience)
            }
        }

        private val CONTENT_SECURITY_POLICY = """
    default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; img-src 'self' data:; font-src 'self' data: https://fonts.gstatic.com;
        """.trimIndent()
    }
}
