package io.mindsync.users.application.command

import io.mindsync.common.domain.command.Command
import io.mindsync.users.domain.Credential
import io.mindsync.users.domain.User
import java.util.*

/**
 * Represents a command to register a new user.
 *
 * @created 8/7/23
 * @property email The email of the new user.
 * @property password The password of the new user.
 * @property firstname The first name of the new user.
 * @property lastname The last name of the new user.
 * @property id The unique identifier of the command.
 */
data class RegisterUserCommand(
    val email: String,
    val password: String,
    val firstname: String,
    val lastname: String
) : Command {
    /**
     * Converts the current object to a User object.
     *
     * @return The converted User object.
     * @see User for more information about the User object.
     */
    fun toUser(): User {
        return User(
            id = UUID.randomUUID(),
            email = email,
            firstName = firstname,
            lastName = lastname,
            credentials = mutableListOf(Credential.create(password))
        )
    }

    override val id: UUID = UUID.randomUUID()
}
