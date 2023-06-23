@file:Suppress("UNCHECKED_CAST")

package io.mindsync.authentication.infrastructure

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.stream.Collectors

internal object Claims {
    const val CLAIMS_NAMESPACE = "https://www.mindsync.io/"
    fun extractAuthorityFromClaims(claims: Map<String, Any>): List<GrantedAuthority> {
        return mapRolesToGrantedAuthorities(getRolesFromClaims(claims))
    }

    private fun getRolesFromClaims(claims: Map<String, Any>): Collection<String> {
        return (
            claims["groups"] ?: (
                claims["roles"] ?: (
                    claims[CLAIMS_NAMESPACE + "roles"]
                        ?: ArrayList<Any>()
                    )
                )
            ) as Collection<String>
    }

    private fun mapRolesToGrantedAuthorities(roles: Collection<String>): List<GrantedAuthority> {
        return roles.stream().filter { role: String ->
            role.startsWith(
                "ROLE_"
            )
        }.map { role: String? ->
            SimpleGrantedAuthority(
                role
            )
        }.collect(Collectors.toList())
    }
}
