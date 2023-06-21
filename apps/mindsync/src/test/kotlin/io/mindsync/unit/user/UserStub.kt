package io.mindsync.unit.user

import io.mindsync.user.domain.*
import net.datafaker.Faker
import java.util.*

class UserStub {
    companion object {
    private val faker = Faker()
        fun create(): User {
            return User(
                id = UserId(UUID.randomUUID()),
                email = Email(faker.internet().emailAddress()),
                name = Name(faker.name().firstName(), faker.name().lastName()),
                password = Password(faker.internet().password()),
            )
        }
    }
}
