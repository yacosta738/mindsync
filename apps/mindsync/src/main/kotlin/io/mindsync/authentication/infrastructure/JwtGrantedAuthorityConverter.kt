package io.mindsync.authentication.infrastructure

import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component

@Component
class JwtGrantedAuthorityConverter :
    Converter<Jwt, Collection<GrantedAuthority>> {
    override fun convert(jwt: Jwt): Collection<GrantedAuthority> {
        return Claims.extractAuthorityFromClaims(jwt.claims)
    }
}
