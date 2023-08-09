package io.mindsync.authentication.application.query

import io.mindsync.common.domain.query.Query
import java.util.*

/**
 *
 * @created 31/7/23
 */
data class AuthenticateUserQuery(
    override val id: UUID = UUID.randomUUID(),
    val username: String,
    val password: String
) : Query
