package io.mindsync.users.domain.exceptions

import io.mindsync.common.domain.error.BusinessRuleValidationException

sealed class InvalidArgumentEmailException(
    override val message: String,
    override val cause: Throwable? = null
) : BusinessRuleValidationException(message, cause)

data class EmailNotValidException(val id: String, override val cause: Throwable? = null) :
    InvalidArgumentEmailException("The email <$id> is not valid", cause)
