package io.mindsync.users.domain

import io.mindsync.common.domain.query.Response

/**
 * Represents a response from the API with the data and the errors
 * @created 3/7/23
 */
data class ApiError(val message: String)

/**
 * Represents a response from the API with the data and the errors
 * @param data the data of the response
 * @param apiErrors the errors of the response
 * @param <T> the type of the data
 * @constructor Creates a new response
 * @property data the data of the response
 * @property apiErrors the errors of the response
 * @see ApiError the error of the response data
 */
data class ApiResponse<T>(val data: T?, val apiErrors: List<ApiError>) : Response {
    companion object {
        /**
         * Creates a new response with the data
         * @param data the data of the response
         * @return a new response with the data
         * @param <T> the type of the data
         * @see ApiError the error of the response data
         * @see ApiResponse the response
         * @see ApiError the error of the response data
         */
        fun <T> success(data: T): ApiResponse<T> {
            return ApiResponse(data, emptyList())
        }

        /**
         * Creates a new response with the errors
         * @param apiErrors the errors of the response
         * @return a new response with the errors
         * @param <T> the type of the data
         * @see ApiError the error of the response data
         * @see ApiResponse the response
         * @see ApiError the error of the response data
         */
        fun <T> error(apiErrors: List<ApiError>): ApiResponse<T> {
            return ApiResponse(null, apiErrors)
        }
    }
}
