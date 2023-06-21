package io.mindsync.authentication.infrastructure

import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.util.Assert


internal class AudienceValidator(allowedAudience: List<String>) : OAuth2TokenValidator<Jwt> {
  private val log = LoggerFactory.getLogger(AudienceValidator::class.java)
  private val error = OAuth2Error("invalid_token", "The required audience is missing", null)
  private val allowedAudience: List<String>

  init {
    Assert.notEmpty(allowedAudience, "Allowed audience should not be null or empty.")
    this.allowedAudience = allowedAudience
  }

  override fun validate(jwt: Jwt): OAuth2TokenValidatorResult {
    val audience = jwt.audience
    if (audience.stream().anyMatch { o: String ->
        allowedAudience.contains(
          o
        )
      }) {
      return OAuth2TokenValidatorResult.success()
    }
    log.warn("Invalid audience: {}", audience)
    return OAuth2TokenValidatorResult.failure(error)
  }
}


