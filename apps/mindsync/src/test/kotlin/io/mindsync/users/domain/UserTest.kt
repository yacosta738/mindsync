package io.mindsync.users.domain

import io.mindsync.UnitTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

@UnitTest
internal class UserTest {
    private val email = "test@mindsync.com"
    private val username = "mindsync"
    private val firstname = "Mindsync"
    private val lastname = "Mindsync"
    private val password = "Th1sIsA$5tr0ngP@ssw0rd"

    @Test
    fun `should create a valid user`() {
        val user = User.create(email, username, firstname, lastname)
        assertEquals(email, user.email.value)
        assertEquals(username, user.username.value)
        assertEquals(firstname, user.name?.firstName?.value)
        assertEquals(lastname, user.name?.lastName?.value)
    }

    @Test
    fun `should update user name`() {
        val user = User.create(email, username, firstname, lastname)
        val newFirstName = "Mindsync"
        val newLastName = "Mindsync"
        user.updateName(newFirstName, newLastName)
        assertEquals(newFirstName, user.name?.firstName?.value)
        assertEquals(newLastName, user.name?.lastName?.value)
    }

    @Test
    fun `should create a valid user with password`() {
        val credential = Credential.create(password, CredentialType.PASSWORD)
        val user = User(
            id = UUID.randomUUID(),
            email = email,
            username = username,
            firstName = firstname,
            lastName = lastname,
            credentials = mutableListOf(credential)
        )
        user.credentials.add(credential)
        assertEquals(email, user.email.value)
        assertEquals(username, user.username.value)
        assertEquals(firstname, user.name?.firstName?.value)
        assertEquals(lastname, user.name?.lastName?.value)
        assertEquals(credential, user.credentials.first())
    }

    @Test
    fun `should return full name`() {
        val user = User.create(email, username, firstname, lastname)
        assertEquals("$firstname $lastname", user.fullName())
    }
}
