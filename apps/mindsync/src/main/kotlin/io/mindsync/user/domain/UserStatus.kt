package io.mindsync.user.domain

enum class UserStatus(val status: String) {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    DELETED("DELETED"),
    BLOCKED("BLOCKED");

    companion object {
        fun fromStatus(status: String): UserStatus? {
            return values().find { it.status == status }
        }
    }
}

