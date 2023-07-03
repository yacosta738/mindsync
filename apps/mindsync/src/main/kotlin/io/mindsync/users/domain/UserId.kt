package io.mindsync.users.domain

import io.mindsync.common.domain.BaseId
import java.util.*

class UserId(private val id: UUID) : BaseId<UUID>(id) {
    constructor(id: String) : this(UUID.fromString(id))
}
