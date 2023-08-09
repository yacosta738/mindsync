package io.mindsync.authentication.application

import io.kotest.common.concurrentHashMap
import io.mindsync.authentication.domain.AccessToken
import io.mindsync.authentication.domain.UserAuthenticator
import io.mindsync.authentication.domain.Username
import io.mindsync.users.domain.Credential
import io.mindsync.users.domain.exceptions.UserAuthenticationException

/**
 * InMemoryUserAuthenticator is a class that implements the [UserAuthenticator] interface.
 * It provides the functionality to authenticate a user with a given username and password.
 * @created 2/8/23
 */
class InMemoryUserAuthenticator(
    private val database: MutableMap<String, String> = concurrentHashMap()
) : UserAuthenticator {
    /**
     * Login a user with the given username and password.
     *
     * @param username the username of the user to be logged in
     * @param password the password of the user to be logged in
     * @return the access token of the user
     */
    override suspend fun authenticate(username: Username, password: Credential): AccessToken {
        return if (database[username.value] == password.value) {
            AccessToken(
                "token",
                1,
                "refreshToken",
                1,
                "type",
                1,
                "81945a53-7bfa-4347-9321-b46c2a2a736d",
                "email profile"
            )
        } else {
            throw UserAuthenticationException("Invalid account. User probably hasn't verified email.")
        }
    }

    /**
     * Adds a user to the database.
     *
     * @param username the username of the user to be added
     * @param password the password of the user to be added
     */
    fun addUser(username: String, password: String) = database.put(username, password)

    /**
     * Clears the database.
     */
    fun clear() = database.clear()
}
