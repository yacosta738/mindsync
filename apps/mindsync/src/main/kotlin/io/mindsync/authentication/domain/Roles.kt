package io.mindsync.authentication.domain

import java.util.stream.Stream

data class Roles(val roles: Set<Role>) {
  fun hasRole(): Boolean {
    return roles.isNotEmpty()
  }

  fun hasRole(role: Role): Boolean {
    return roles.contains(role)
  }

  fun hasAnyRole(vararg roles: Role): Boolean {
    return roles.any { hasRole(it) }
  }

  fun stream(): Stream<Role> {
    return roles.stream()
  }

  companion object {
    val EMPTY = Roles(setOf())
  }
}
