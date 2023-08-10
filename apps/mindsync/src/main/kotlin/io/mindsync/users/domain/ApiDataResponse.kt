package io.mindsync.users.domain

import io.mindsync.common.domain.query.Response
enum class ApiResponseStatus {
    SUCCESS,
    FAILURE
}

data class ApiDataResponse<T>(
    val status: ApiResponseStatus,
    val data: T? = null,
    val error: String? = null
) : Response {
    companion object {
        fun <T> success(data: T): ApiDataResponse<T> {
            return ApiDataResponse(ApiResponseStatus.SUCCESS, data)
        }

        fun <T> failure(error: String): ApiDataResponse<T> {
            return ApiDataResponse(ApiResponseStatus.FAILURE, error = error)
        }
    }
}
