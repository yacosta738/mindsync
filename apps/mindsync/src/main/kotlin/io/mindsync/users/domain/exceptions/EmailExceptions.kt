package io.mindsync.users.domain.exceptions

import io.mindsync.common.domain.error.BusinessRuleValidationException
/**
 * Invalid argument email exception is thrown when an email is not valid.
 * @author Yuniel Acosta (acosta)
 * @created 2/7/23
 */
sealed class InvalidArgumentEmailException(
    override val message: String,
    override val cause: Throwable? = null
) : BusinessRuleValidationException(message, cause)

/**
 * Email not valid exception is thrown when an email is not valid.
 * @param id The email id.
 * @param cause The cause of the exception.
 */
data class EmailNotValidException(val id: String, override val cause: Throwable? = null) :
    InvalidArgumentEmailException("The email <$id> is not valid", cause)
