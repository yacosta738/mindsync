package io.mindsync.authentication.application

import io.mindsync.authentication.domain.Role
import io.mindsync.authentication.domain.Roles
import io.mindsync.authentication.domain.Username
import io.mindsync.authentication.domain.error.NotAuthenticatedUserException
import io.mindsync.authentication.domain.error.UnknownAuthenticationException
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import java.util.*
import java.util.stream.Collectors

/**

This is an utility class to get authenticated user information
 */
object AuthenticatedUser {
  private const val PREFERRED_USERNAME = "preferred_username"

  /**

  Get the authenticated user username
  @return The authenticated user username
  @throws NotAuthenticatedUserException if the user is not authenticated
  @throws UnknownAuthenticationException if the user uses an unknown authentication scheme
   */
  fun username(): Username {
    return optionalUsername().orElseThrow(::NotAuthenticatedUserException)
  }
  /**

  Get the authenticated user username
  @return The authenticated user username or empty if the user is not authenticated
  @throws UnknownAuthenticationException if the user uses an unknown authentication scheme
   */
  fun optionalUsername(): Optional<Username> {
    return authentication().map(::readPrincipal).flatMap(Username::of)
  }
  /**

  Read user principal from authentication

  @param authentication authentication to read the principal from

  @return The user principal

  @throws UnknownAuthenticationException if the authentication can't be read (unknown token type)
   */
  fun readPrincipal(authentication: Authentication): String {

    return when {
      authentication.principal is UserDetails -> (authentication.principal as UserDetails).username
      authentication is JwtAuthenticationToken -> authentication.token.claims[PREFERRED_USERNAME]?.toString() ?: ""
      authentication.principal is DefaultOidcUser -> (authentication.principal as DefaultOidcUser).attributes[PREFERRED_USERNAME]?.toString() ?: ""
      authentication.principal is String -> authentication.principal as String
      else -> throw UnknownAuthenticationException()
    }
  }

  /**

  Get the authenticated user roles
  @return The authenticated user roles or empty roles if the user is not authenticated
   */
  fun roles(): Roles {
    return authentication().map(::toRoles).orElse(Roles.EMPTY)
  }
  private fun toRoles(authentication: Authentication): Roles {
    return Roles(
      authentication.authorities.stream()
        .map(GrantedAuthority::getAuthority)
        .map(Role::from)
        .collect(Collectors.toSet())
    )
  }

  /**

  Get the authenticated user token attributes

  @return The authenticated user token attributes

  @throws NotAuthenticatedUserException if the user is not authenticated

  @throws UnknownAuthenticationException if the authentication scheme is unknown
   */
  fun attributes(): Map<String, Any> {

    return when (val token = authentication().orElseThrow(::NotAuthenticatedUserException)) {
        is OAuth2AuthenticationToken -> token.principal.attributes
      is JwtAuthenticationToken -> token.tokenAttributes
      else -> throw UnknownAuthenticationException()
    }
  }

  private fun authentication(): Optional<Authentication> {
    return Optional.ofNullable(SecurityContextHolder.getContext().authentication)
  }
}
