package io.mindsync.authentication.infrastructure

import io.mindsync.UnitTest
import io.mindsync.authentication.domain.Role
import io.mindsync.authentication.infrastructure.Claims.extractAuthorityFromClaims
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.security.core.authority.SimpleGrantedAuthority


@UnitTest
internal class ClaimsTest {
  @Test
  fun shouldExtractAuthorityFromClaims() {
    val claims: Map<String, Any> = mapOf(CLAIMS_NAMESPACE + "roles" to listOf(Role.ADMIN.key(), Role.USER.key()))

    val authorities = extractAuthorityFromClaims(claims)
    assertThat(authorities).containsExactly(
      SimpleGrantedAuthority(Role.ADMIN.key()),
      SimpleGrantedAuthority(Role.USER.key())
    )
  }

  @Test
  fun shouldExtractAuthorityFromClaimsNamespacedRoles() {
    val claims: Map<String, Any> = mapOf(CLAIMS_NAMESPACE + "roles" to listOf(Role.ADMIN.key(), Role.USER.key()))
    val authorities = extractAuthorityFromClaims(claims)
    assertThat(authorities).containsExactly(
      SimpleGrantedAuthority(Role.ADMIN.key()),
      SimpleGrantedAuthority(Role.USER.key())
    )
  }

  companion object {
    private const val CLAIMS_NAMESPACE = "https://www.mindsync.io/"
  }
}

