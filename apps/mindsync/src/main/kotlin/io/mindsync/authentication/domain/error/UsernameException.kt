package io.mindsync.authentication.domain.error

import io.mindsync.common.domain.error.InvalidArgumentException

class UsernameException(override val message: String, override val cause: Throwable? = null): InvalidArgumentException(message, cause)
