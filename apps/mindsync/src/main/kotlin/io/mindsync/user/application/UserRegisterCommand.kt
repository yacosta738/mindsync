package io.mindsync.user.application

import io.mindsync.user.domain.*
import java.util.*

class UserRegisterCommand(
     val email: String,
     val password: String,
     val firstName: String,
     val lastName: String
){
    fun toUser(): User {
        return User(
            id = UserId(UUID.randomUUID()),
            email = Email(email),
            password = Password(password),
            name = Name(firstName, lastName)
        )
    }
}
