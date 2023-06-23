package io.mindsync.authentication.domain

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
        private val ROLES: Map<String, Role> = buildRoles()
        private fun buildRoles(): Map<String, Role> = values().associateBy { it.key() }

        fun from(role: String): Role {
            if (role.isBlank()) {
                return UNKNOWN
            }
            return ROLES[role] ?: UNKNOWN
        }
    }
}
