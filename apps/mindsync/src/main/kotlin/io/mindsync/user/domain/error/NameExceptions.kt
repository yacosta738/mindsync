package io.mindsync.user.domain.error

import io.mindsync.common.domain.error.InvalidArgumentException

data class FirstNameNotValidException(val id: String, override val cause: Throwable? = null) :
    InvalidArgumentException("The first name <$id> is not valid", cause)

data class LastNameNotValidException(val id: String, override val cause: Throwable? = null) :
    InvalidArgumentException("The last name <$id> is not valid", cause)
