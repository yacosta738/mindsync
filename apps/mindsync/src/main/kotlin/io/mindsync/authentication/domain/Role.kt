package io.mindsync.authentication.domain

import java.util.stream.Collectors
import java.util.stream.Stream


enum class Role {
  ADMIN,
  USER,
  ANONYMOUS,
  UNKNOWN;

  fun key(): String {
    return PREFIX + name
  }

  companion object {
    private const val PREFIX = "ROLE_"
    private val ROLES = buildRoles()
    private fun buildRoles(): Map<String, Role> = Stream.of(*values())
        .collect(Collectors.toMap({ obj: Role -> obj.key() }, { obj: Role -> obj }))

    fun from(role: String): Role {
      if(role.isBlank()){
        return UNKNOWN
      }
      return ROLES.getOrDefault(role, UNKNOWN)
    }
  }
}

