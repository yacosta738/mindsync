package io.mindsync.crypto.application

import io.mindsync.crypto.domain.EncryptionService
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * A class that provides AES encryption and decryption services.
 *
 * @param salt the salt string used for key derivation
 * @param key the key string used for encryption and decryption
 *
 * @author Yuniel Acosta
 */
class AesEncryptionService(salt: String, key: String) :
    EncryptionService {
    private val saltBytes: ByteArray = hexStringToByteArray(salt)
    private val factory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM)
    private val keySpec = PBEKeySpec(key.toCharArray(), saltBytes, ITERATION_COUNT, KEY_LENGTH)
    private val secretKey = factory.generateSecret(keySpec)
    private val secretKeySpec = SecretKeySpec(secretKey.encoded, "AES")
    private val random = SecureRandom()

    companion object {
        private const val ITERATION_COUNT = 65536
        private const val KEY_LENGTH = 256
        private const val GCM_TAG_LENGTH = 128
        private const val CIPHER_TRANSFORMATION = "AES/GCM/NoPadding"
        private const val SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA1"
        private const val RADIX = 16
        private const val IV_BYTE_LENGTH = 8
    }

    /**
     * Encrypts the given plain text value.
     *
     * @param aPlainTextValue the plain text value to encrypt
     * @return the encrypted value
     */
    override fun encrypt(aPlainTextValue: String): String {
        val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
        val ivParams = ByteArray(GCM_TAG_LENGTH / IV_BYTE_LENGTH)
        random.nextBytes(ivParams)
        val gcmParams = GCMParameterSpec(GCM_TAG_LENGTH, ivParams)
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParams)

        val encryptedBytes = cipher.doFinal(aPlainTextValue.toByteArray())
        val cipherText = ByteArray(ivParams.size + encryptedBytes.size)
        System.arraycopy(ivParams, 0, cipherText, 0, ivParams.size)
        System.arraycopy(encryptedBytes, 0, cipherText, ivParams.size, encryptedBytes.size)

        return Base64.getEncoder().encodeToString(cipherText)
    }

    /**
     * Decrypts the given encrypted value.
     *
     * @param encryptedData the encrypted value to decrypt
     * @return the decrypted value
     */
    override fun decrypt(encryptedData: String): String {
        val cipherText = Base64.getDecoder().decode(encryptedData)
        val ivParams = cipherText.copyOfRange(0, GCM_TAG_LENGTH / IV_BYTE_LENGTH)
        val encryptedBytes = cipherText.copyOfRange(GCM_TAG_LENGTH / IV_BYTE_LENGTH, cipherText.size)

        val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
        val gcmParams = GCMParameterSpec(GCM_TAG_LENGTH, ivParams)
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParams)

        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes)
    }

    /**
     * Converts a hexadecimal string to a byte array.
     *
     * @param hexString The hexadecimal string to convert.
     * @return The byte array representing the hexadecimal string.
     */
    private fun hexStringToByteArray(hexString: String): ByteArray {
        val len = hexString.length / 2
        val result = ByteArray(len)
        for (i in 0 until len) {
            val index = i * 2
            val byteString = hexString.substring(index, index + 2)
            val byteValue = byteString.toInt(RADIX)
            result[i] = byteValue.toByte()
        }
        return result
    }
}
