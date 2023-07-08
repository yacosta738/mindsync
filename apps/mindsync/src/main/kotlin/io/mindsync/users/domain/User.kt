package io.mindsync.users.domain

import io.mindsync.authentication.domain.Username
import io.mindsync.common.domain.AggregateRoot
import java.util.*

/**
 * User domain model. This is the root of the user aggregate. It contains all the information about the user.
 * @author Yuniel Acosta (acosta)
 * @created 2/7/23
 * @param id the user id as a string representation of a UUID value (e.g. "123e4567-e89b-12d3-a456-426614174000")
 * @param username the username as a string value (e.g. "username") see [Username] for more information
 * @param email the email as a string value (e.g. "email") see [Email] for more information
 * @param name the name as a string value (e.g. "name") see [Name] for more information
 * @param credentials the credentials as a list of [Credential] see [Credential] for more information
 * @see AggregateRoot for more information about the aggregate root domain model
 * @see UserId for more information about the user id
 * @see Username for more information about the username
 * @see Email for more information about the email
 * @see Name for more information about the name
 * @see Credential for more information about the credential
 * @see CredentialType for more information about the credential type
 */
data class User(
    override val id: UserId,
    val username: Username,
    val email: Email,
    var name: Name? = null,
    val credentials: MutableList<Credential> = mutableListOf()
) : AggregateRoot<UserId>() {

    /**
     * Constructor for the user domain model.
     * @param id the user id as a string representation of a UUID value (e.g. "123e4567-e89b-12d3-a456-426614174000")
     *           or a UUID value (e.g. UUID.randomUUID()) see [UUID] for more information
     * @param username the username as a string value (e.g. "username") see [Username] for more information
     * @param email the email as a string value (e.g. "test@domain.com") see [Email] for more information
     * @param firstName the first name as a string value (e.g. "John") see [Name] for more information
     * @param lastName the last name as a string value (e.g. "Doe") see [Name] for more information
     * @param credentials the credentials as a list of [Credential] see [Credential] for more information
     * @see UUID for more information about the UUID
     * @see Username for more information about the username
     * @see Email for more information about the email
     * @see Name for more information about the name
     * @see Credential for more information about the credential
     * @see CredentialType for more information about the credential type
     * @see AggregateRoot for more information about the aggregate root
     * @see UserId for more information about the user id
     */
    constructor(
        id: UUID,
        username: String,
        email: String,
        firstName: String,
        lastName: String,
        credentials: MutableList<Credential> = mutableListOf()
    ) : this(UserId(id), Username(username), Email(email), Name(firstName, lastName), credentials)

    /**
     * Full name of the user. It is the concatenation of the first name and the last name.
     * @return the full name of the user as a string value (e.g. "John Doe") or an empty string if the name is null
     * @see Name for more information about the name
     * @see Name.fullName for more information about the full name
     * @see User.name for more information about the User's name
     */
    fun fullName(): String {
        return name?.fullName() ?: ""
    }

    /**
     * Update the name of the user.
     *
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     */
    fun updateName(firstName: String, lastName: String) {
        name = Name(firstName, lastName)
    }

    companion object {
        /**
         * Creates a new User with the given information.
         *
         * @param email the email address of the user
         * @param username the username of the user
         * @param firstName the first name of the user
         * @param lastName the last name of the user
         * @return the newly created User object
         */
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
