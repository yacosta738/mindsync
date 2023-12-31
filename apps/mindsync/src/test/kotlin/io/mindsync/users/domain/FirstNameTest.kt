package io.mindsync.users.domain

import io.mindsync.UnitTest
import io.mindsync.users.domain.exceptions.FirstNameNotValidException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

@UnitTest
internal class FirstNameTest {
    @Test
    fun `should create a valid first name`() {
        val firstName = FirstName("Yuniel")
        assertEquals("Yuniel", firstName.value)
    }

    @Test
    fun `should throw an exception when create a first name with invalid value`() {
        val invalidFirstNames = listOf(
            "123abc", "abc!@#", "abc.def", "abc-def",
            "abc_def", "abc+def", "abc*def", "abc/def", "abc=def", "abc?def",
            "abc#def", "abc\$def", "abc%def", "abc^def", "abc&def", "abc(def",
            "abc)def", "abc[def", "abc]def", "abc{def", "abc}def", "abc:def",
            "abc;def", "abc\"def", "abc'def", "abc<def", "abc>def", "abc,def",
            "abc~def", "abc`def", "abc|def", "abc\\def", "abc¡def", "abc¿def",
            "abc©def", "abc®def", "abc™def", "abc€def", "abc£def", "abc¥def",
            "abc¢def", "abc§def", "abc¶def", "abc•def", "abc†def", "abc‡def",
            "abc°def", "abc±def", "abc¶def", "abc•def", "abc†def", "abc‡def",
            "abc²def", "abc³def", "abc¼def", "abc½def", "yuniel-acosta", "yuniel_acosta"
        )
        invalidFirstNames.forEach {
            assertThrows(FirstNameNotValidException::class.java) {
                FirstName(it)
            }
        }
    }

    @Test
    fun `should throw an exception when create a first name with empty value`() {
        assertThrows(FirstNameNotValidException::class.java) {
            FirstName("")
        }
    }

    @Test
    fun `should throw an exception when create a first name with blank value`() {
        assertThrows(FirstNameNotValidException::class.java) {
            FirstName(" ")
        }
    }

    @Test
    fun `should throw an exception when create a first name with length greater than 150`() {
        val firstName = (1..256).joinToString("") { "a" }
        assertThrows(FirstNameNotValidException::class.java) {
            FirstName(firstName)
        }
    }

    @Test
    fun `compare first name`() {
        val firstName1 = FirstName("Yuniel")
        val firstName2 = FirstName("Yuniel")
        assertEquals(firstName1, firstName2)
        assertEquals(firstName1.hashCode(), firstName2.hashCode())
    }

    @Test
    fun `compare first name with different values`() {
        val firstName1 = FirstName("Yuniel")
        val firstName2 = FirstName("David")
        assertNotEquals(firstName1, firstName2)
        assertNotEquals(firstName1.hashCode(), firstName2.hashCode())
    }
}
