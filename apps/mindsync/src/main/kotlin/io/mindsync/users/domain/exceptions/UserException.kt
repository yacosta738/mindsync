package io.mindsync.users.domain.exceptions

import io.mindsync.common.domain.error.BusinessRuleValidationException

/**
 *
 * @author acosta
 * @created 29/6/23
 */

open class UserException(
    override val message: String,
    override val cause: Throwable? = null
) : BusinessRuleValidationException(message, cause)
class UserNotFoundException(userId: String) : UserException("The user <$userId> does not exist")
class UserStoreException(message: String, cause: Throwable? = null) :
    UserException(message, cause)

class CredentialException(message: String) : UserException(message)
