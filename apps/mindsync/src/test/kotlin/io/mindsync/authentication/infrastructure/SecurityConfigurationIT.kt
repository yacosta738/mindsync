package io.mindsync.authentication.infrastructure

import io.mindsync.IntegrationTest
import io.mindsync.authentication.domain.Role
import org.assertj.core.api.Assertions.*
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.OidcUserInfo
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority
import java.time.Instant


@IntegrationTest
internal class SecurityConfigurationIT {
  @Autowired
  private lateinit var grantedAuthoritiesMapper: GrantedAuthoritiesMapper
  @Test
  fun shouldUserAuthoritiesMapperWithOidcUserAuthority() {
    val claims: MutableMap<String, Any> = HashMap()
    claims["groups"] = listOf(Role.USER.key())
    claims["sub"] = 123
    claims["preferred_username"] = "admin"
    val idToken = OidcIdToken(ID_TOKEN, Instant.now(), Instant.now().plusSeconds(60), claims)
    val userInfo = OidcUserInfo(claims)
    val authorities: MutableCollection<GrantedAuthority> = ArrayList()
    authorities.add(OidcUserAuthority(Role.USER.key(), idToken, userInfo))
    assertThatCode { grantedAuthoritiesMapper.mapAuthorities(authorities) }.doesNotThrowAnyException()
  }

  @Test
  fun shouldUserAuthoritiesMapperWithSimpleGrantedAuthority() {
    val authorities: MutableCollection<GrantedAuthority> = ArrayList()
    authorities.add(SimpleGrantedAuthority(Role.USER.key()))
    assertThatCode { grantedAuthoritiesMapper.mapAuthorities(authorities) }.doesNotThrowAnyException()
  }
}

