package io.mindsync.users.domain

import io.mindsync.common.domain.query.Response
enum class ApiResponseStatus {
    SUCCESS,
    FAILURE
}

data class ApiResponse<T>(
    val status: ApiResponseStatus,
    val data: T? = null,
    val error: String? = null
) : Response {
    companion object {
        fun <T> success(data: T): ApiResponse<T> {
            return ApiResponse(ApiResponseStatus.SUCCESS, data)
        }

        fun <T> failure(error: String): ApiResponse<T> {
            return ApiResponse(ApiResponseStatus.FAILURE, error = error)
        }
    }
}
