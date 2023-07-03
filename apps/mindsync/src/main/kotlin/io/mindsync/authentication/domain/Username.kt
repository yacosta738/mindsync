package io.mindsync.authentication.domain

import io.mindsync.authentication.domain.error.UsernameException
import io.mindsync.common.domain.BaseValidateValueObject
import java.util.*

private const val MAX_LENGTH = 100
private const val MIN_LENGTH = 3

data class Username(val username: String) : BaseValidateValueObject<String>(username) {

    override fun validate(value: String) {
        if (value.isBlank()) {
            throw UsernameException("Username cannot be blank")
        }
        if (value.length !in (MIN_LENGTH)..MAX_LENGTH) {
            throw UsernameException("Username must be between $MIN_LENGTH and $MAX_LENGTH characters")
        }
    }

    companion object {
        fun of(username: String): Optional<Username> {
            return try {
                Optional.of(Username(username))
            } catch (_: UsernameException) {
                Optional.empty()
            }
        }
    }
}
