package io.mindsync.users.application.command

import io.mindsync.common.domain.command.Command
import io.mindsync.users.domain.Credential
import io.mindsync.users.domain.User
import java.util.*

data class RegisterUserCommand(
    val username: String,
    val email: String,
    val password: String,
    val firstname: String,
    val lastname: String
) : Command {
    fun toUser(): User {
        return User(
            id = UUID.randomUUID(),
            username = username,
            email = email,
            firstName = firstname,
            lastName = lastname,
            credentials = mutableListOf(Credential.create(password))
        )
    }

    override val id: UUID = UUID.randomUUID()
}
