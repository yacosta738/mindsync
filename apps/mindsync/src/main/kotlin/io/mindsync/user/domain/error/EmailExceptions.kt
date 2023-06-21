package io.mindsync.user.domain.error

import io.mindsync.common.domain.error.InvalidArgumentException

data class EmailNotValidException(val id: String, override val cause: Throwable? = null) :
    InvalidArgumentException("The email <$id> is not valid", cause)
