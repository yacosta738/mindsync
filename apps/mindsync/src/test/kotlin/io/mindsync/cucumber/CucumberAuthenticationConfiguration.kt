package io.mindsync.cucumber

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import java.time.Instant


@TestConfiguration
class CucumberAuthenticationConfiguration {
  @Bean
  @Primary
  fun jwtDecoder(): JwtDecoder {
    val decoder = Jwts.parserBuilder().setSigningKey(JWT_KEY).build()
    return JwtDecoder { token: String? ->
      Jwt(
        "token",
        Instant.now(),
        Instant.now().plusSeconds(120),
        mapOf("issuer" to "http://dev"),
        decoder.parseClaimsJws(token).body
      )
    }
  }

  companion object {
    val JWT_KEY = Keys.hmacShaKeyFor(
      Decoders.BASE64.decode("OTdhNzE2OTQwNWVmYmZhOWRiOTA4MzI2ZDRmNDg1NzMwNDlhNDZmMQ==")
    )
  }
}

