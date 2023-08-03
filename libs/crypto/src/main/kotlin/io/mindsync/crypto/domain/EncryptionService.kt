package io.mindsync.crypto.domain

/**
 * Service for encrypting values.
 *
 */
interface EncryptionService {
    /**
     * Encrypts the given plain text value.
     *
     * @param aPlainTextValue the plain text value to encrypt
     * @return the encrypted value
     */
    fun encrypt(aPlainTextValue: String): String

    /**
     * Decrypts the given encrypted value.
     *
     * @param encryptedData the encrypted value to decrypt
     * @return the decrypted value
     */
    fun decrypt(encryptedData: String): String
}
