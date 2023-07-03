package io.mindsync.users.domain

/**
 * Represents a response from the API with the data and the errors
 * @author Yuniel Acosta (acosta)
 * @created 3/7/23
 */

data class ApiError(val message: String)
data class Response<T>(val data: T?, val apiErrors: List<ApiError>) {
    companion object {
        fun <T> success(data: T): Response<T> {
            return Response(data, emptyList())
        }

        fun <T> error(apiErrors: List<ApiError>): Response<T> {
            return Response(null, apiErrors)
        }
    }
}
