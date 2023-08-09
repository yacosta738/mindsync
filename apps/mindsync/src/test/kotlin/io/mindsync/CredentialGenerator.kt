package io.mindsync

import io.mindsync.users.domain.Credential
import io.mindsync.users.domain.CredentialId
import java.util.*

/**
 * CredentialGenerator is a utility class for generating credentials.
 * @created 2/8/23
 */
object CredentialGenerator {
    fun generate(password: String = Credential.generateRandomCredentialPassword()): Credential =
        Credential(CredentialId(UUID.randomUUID()), password)
}
