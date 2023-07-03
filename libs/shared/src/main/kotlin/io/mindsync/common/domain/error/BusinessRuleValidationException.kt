package io.mindsync.common.domain.error

abstract class BusinessRuleValidationException(
    override val message: String,
    override val cause: Throwable? = null
) : RuntimeException(message, cause)
