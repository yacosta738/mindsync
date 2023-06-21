package io.mindsync.user.domain

import io.mindsync.common.domain.BaseId
import io.mindsync.user.domain.error.InvalidUserIdException
import java.util.*

data class UserId(val id: UUID): BaseId<UUID>(id) {
    companion object {
        fun fromString(id: String): UserId {
            return try {
                UserId(UUID.fromString(id))
            } catch (e: Exception) {
                throw InvalidUserIdException(id, e)
            }
        }
    }
}
