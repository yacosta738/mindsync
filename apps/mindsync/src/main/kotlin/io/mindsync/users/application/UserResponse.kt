package io.mindsync.users.application
data class UserResponse(
    val username: String,
    val email: String,
    val firstname: String?,
    val lastname: String?
)
