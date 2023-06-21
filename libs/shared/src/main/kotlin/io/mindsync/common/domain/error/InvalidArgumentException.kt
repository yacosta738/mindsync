package io.mindsync.common.domain.error
 abstract class InvalidArgumentException(
    override val message: String,
    override val cause: Throwable? = null
) : IllegalArgumentException(message, cause)
