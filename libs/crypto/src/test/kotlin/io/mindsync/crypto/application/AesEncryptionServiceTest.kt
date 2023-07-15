package io.mindsync.crypto.application

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class AesEncryptionServiceTest {
    private val salt = "1234567890123456"
    private val key = "12345678901234567890123456789012"
    private val tokenEncryptionService = AesEncryptionService(salt, key)

    private val valuesToEncrypt = listOf(
        "This is a test",
        "This is a test with a long text to encrypt",
        "This is a test with a long text to encrypt and a special character: !@#$%^&*()_+{}[]|\\:;\"'<>,.?/"
    )

    @Test
    fun `encrypt values with token encryption service`() {
        valuesToEncrypt.forEach {
            println("Value: $it")
            val encryptedValue = tokenEncryptionService.encrypt(it)
            println("Encrypted Value: $encryptedValue")
            val decryptedValue = tokenEncryptionService.decrypt(encryptedValue)
            println("Decrypted Value: $decryptedValue")
            assertEquals(it, decryptedValue)
        }
    }
}
