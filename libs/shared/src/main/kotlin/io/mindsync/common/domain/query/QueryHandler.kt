package io.mindsync.common.domain.query

/**
 * Represents a query handler that handles a specific type of query and returns a response.
 *
 * @param Q The type of the query to be handled.
 * @param R The type of the response.
 * @since 1.0
 * @created 31/7/23
 */
fun interface QueryHandler<Q : Query, R : Response> {
    /**
     * Handles the given query.
     * @param query The query to handle.
     * @return The response of the query.
     */
    suspend fun handle(query: Q): R
}
