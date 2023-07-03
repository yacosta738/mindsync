package io.mindsync.authentication.domain.error

import io.mindsync.common.domain.error.BusinessRuleValidationException

class UsernameException(override val message: String, override val cause: Throwable? = null) :
    BusinessRuleValidationException(message, cause)
