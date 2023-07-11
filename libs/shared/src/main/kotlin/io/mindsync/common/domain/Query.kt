package io.mindsync.common.domain

import java.util.*

/**
 * Represents a query.
 *
 * Query objects are used to retrieve data from a data source.
 *
 * @property id The unique identifier of the query.
 * @author Yuniel Acosta
 * @created 29/6/23
 */
interface Query {
    val id: UUID
}
