package io.mindsync

import io.mindsync.users.domain.Credential
import io.mindsync.users.domain.CredentialId
import java.util.*

/**
 * Class that represents a credential generator.
 * This class is responsible for generating different types of credentials.
 * @created 2/8/23
 */
object CredentialGenerator {
    fun generateCredential(password: String = Credential.generateRandomCredentialPassword()): Credential =
        Credential(CredentialId(UUID.randomUUID()), password)
}
