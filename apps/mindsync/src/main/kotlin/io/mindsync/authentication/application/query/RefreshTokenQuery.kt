package io.mindsync.authentication.application.query

import io.mindsync.common.domain.query.Query
import java.util.*

/**
 *
 * @created 31/7/23
 */
data class RefreshTokenQuery(
    override val id: UUID = UUID.randomUUID(),
    val refreshToken: String,
) : Query
