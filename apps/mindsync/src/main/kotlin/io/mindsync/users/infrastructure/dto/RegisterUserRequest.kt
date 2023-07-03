package io.mindsync.users.infrastructure.dto

import io.mindsync.users.application.command.RegisterUserCommand
import jakarta.validation.constraints.*

/**
 *
 * @author Yuniel Acosta (acosta)
 * @created 2/7/23
 */
data class RegisterUserRequest(
    @field:NotBlank(message = "Username cannot be blank")
    @field:Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    val username: String,
    @field:NotBlank(message = "Email cannot be blank")
    @field:Email(message = "Email must be valid")
    @field:Size(max = 255, message = "Email must be less than 255 characters")
    val email: String,
    @field:NotBlank(message = "Password cannot be blank")
    @field:Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    val password: String,
    @field:NotBlank(message = "Firstname cannot be blank")
    @field:Size(min = 3, max = 100, message = "Firstname must be between 3 and 100 characters")
    val firstname: String,
    @field:NotBlank(message = "Lastname cannot be blank")
    @field:Size(min = 3, max = 100, message = "Lastname must be between 3 and 100 characters")
    val lastname: String
) {
    fun toRegisterUserCommand(): RegisterUserCommand {
        return RegisterUserCommand(
            username,
            email,
            password,
            firstname,
            lastname
        )
    }
}
