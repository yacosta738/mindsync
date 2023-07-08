package io.mindsync.users.domain.exceptions

import io.mindsync.common.domain.error.BusinessRuleValidationException

/**
 * Exception thrown when the user is not valid
 * @author acosta
 * @created 29/6/23
 */

abstract class UserException(
    override val message: String,
    override val cause: Throwable? = null
) : BusinessRuleValidationException(message, cause)

/**
 * Exception thrown when the user is not valid
 * @param message the user that is not valid
 * @param cause the cause of the exception
 */
class UserStoreException(message: String, cause: Throwable? = null) :
    UserException(message, cause)

/**
 * Exception thrown when the user is not valid
 * @param message the user that is not valid
 */
class CredentialException(message: String) : UserException(message)
