package io.mindsync.users.domain.exceptions

import io.mindsync.common.domain.error.BusinessRuleValidationException

sealed class InvalidArgumentNameException(
    override val message: String,
    override val cause: Throwable? = null
) : BusinessRuleValidationException(message, cause)

data class FirstNameNotValidException(val id: String, override val cause: Throwable? = null) :
    InvalidArgumentNameException("The first name <$id> is not valid", cause)

data class LastNameNotValidException(val id: String, override val cause: Throwable? = null) :
    InvalidArgumentNameException("The last name <$id> is not valid", cause)
