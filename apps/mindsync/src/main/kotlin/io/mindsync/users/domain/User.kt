package io.mindsync.users.domain

import io.mindsync.authentication.domain.Username
import io.mindsync.common.domain.AggregateRoot
import java.util.*

data class User(
    override val id: UserId,
    val username: Username,
    val email: Email,
    var name: Name? = null,
    val credentials: MutableList<Credential> = mutableListOf()
) : AggregateRoot<UserId>() {

    constructor(
        id: UUID,
        username: String,
        email: String,
        firstName: String,
        lastName: String,
        credentials: MutableList<Credential> = mutableListOf()
    ) : this(UserId(id), Username(username), Email(email), Name(firstName, lastName), credentials)

    fun fullName(): String {
        return name?.fullName() ?: ""
    }

    fun updateName(firstName: String, lastName: String) {
        name = Name(firstName, lastName)
    }

    companion object {

        fun create(email: String, username: String, firstName: String, lastName: String): User {
            return User(
                UUID.randomUUID(),
                username,
                email,
                firstName,
                lastName,
                mutableListOf(Credential.create(Credential.generateRandomCredentialPassword(), CredentialType.PASSWORD))
            )
        }
    }
}
