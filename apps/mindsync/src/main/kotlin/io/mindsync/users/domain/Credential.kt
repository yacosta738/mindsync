package io.mindsync.users.domain

import io.mindsync.common.domain.BaseId
import io.mindsync.common.domain.BaseValidateValueObject
import io.mindsync.users.domain.exceptions.CredentialException
import java.util.*

/**
 *
 * @author Yuniel Acosta (acosta)
 * @created 2/7/23
 */
data class Credential(
    val id: CredentialId,
    val credentialValue: String,
    val type: CredentialType = CredentialType.PASSWORD
) : BaseValidateValueObject<String>(credentialValue) {
    override fun validate(value: String) {
        if (value.isBlank()) {
            throw CredentialException("Credential value cannot be blank")
        }
        if (value.length < MIN_LENGTH) {
            throw CredentialException("Credential value must be at least $MIN_LENGTH characters")
        }
        // Password must have at least one number, one uppercase, one lowercase and one special character
        if (!value.any { charNumbers.contains(it) }) {
            throw CredentialException("The password must have at least one number")
        }
        if (!value.any { charUppercase.contains(it) }) {
            throw CredentialException("The password must have at least one uppercase character")
        }
        if (!value.any { charLowercase.contains(it) }) {
            throw CredentialException("The password must have at least one lowercase character")
        }
        if (!value.any { charSpecial.contains(it) }) {
            throw CredentialException("The password must have at least one special character")
        }
    }

    companion object {
        const val MIN_LENGTH = 8
        private val charNumbers = '0'..'9'
        private val charUppercase = 'A'..'Z'
        private val charLowercase = 'a'..'z'
        private val charSpecial = "!@#$%^&*()_+{}|:<>?".toList()
        private val charset = charLowercase + charUppercase + charNumbers

        /**
         * Generates a random password
         */
        fun generateRandomCredentialPassword(): String {
            val length = MIN_LENGTH + Random().nextInt(MIN_LENGTH)
            // Must have at least one number, one uppercase, one lowercase and one special character
            val password = (1..length)
                .map { charset.random() }
                .toMutableList()
                .apply {
                    add(charNumbers.random())
                    add(charUppercase.random())
                    add(charLowercase.random())
                    add(charSpecial.random())
                }
                .shuffled()
                .joinToString("")
            return password
        }

        fun create(credentialValue: String, type: CredentialType = CredentialType.PASSWORD): Credential {
            return Credential(CredentialId(UUID.randomUUID()), credentialValue, type)
        }
    }
}

class CredentialId(id: UUID) : BaseId<UUID>(id)

enum class CredentialType {
    PASSWORD,
    TOTP,
    SECRET
}
