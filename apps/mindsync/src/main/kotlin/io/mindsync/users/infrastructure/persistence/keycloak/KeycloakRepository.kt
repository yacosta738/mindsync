package io.mindsync.users.infrastructure.persistence.keycloak

import io.mindsync.authentication.infrastructure.ApplicationSecurityProperties
import io.mindsync.common.domain.error.BusinessRuleValidationException
import io.mindsync.users.domain.Credential
import io.mindsync.users.domain.User
import io.mindsync.users.domain.UserCreator
import io.mindsync.users.domain.UserId
import io.mindsync.users.domain.exceptions.CredentialException
import io.mindsync.users.domain.exceptions.UserException
import io.mindsync.users.domain.exceptions.UserStoreException
import jakarta.ws.rs.ClientErrorException
import jakarta.ws.rs.WebApplicationException
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class KeycloakRepository(
    private val applicationSecurityProperties: ApplicationSecurityProperties,
    private val keycloak: Keycloak
) : UserCreator {
    override suspend fun create(user: User): Mono<User> {
        log.info("Saving user with email: {}", user.email.value)

        return try {
            val password =
                user.credentials.firstOrNull()?.credentialValue ?: Credential.generateRandomCredentialPassword()
            val credentialRepresentation = CredentialRepresentation().apply {
                type = CredentialRepresentation.PASSWORD
                value = password
            }
            checkIfUserAlreadyExists(user)
                .flatMap { userAlreadyExists ->
                    if (userAlreadyExists) {
                        Mono.error(
                            UserStoreException(
                                "User with email: ${user.email.value} or username: ${user.username.value} already exists."
                            )
                        )
                    } else {
                        log.debug(
                            "Trying to create user with email: {} and username: {}",
                            user.email.value,
                            user.username.value
                        )
                        val userRepresentation = getUserRepresentation(user, credentialRepresentation)
                        userRepresentation.username = user.username.value
                        val response = keycloakRealm.users().create(userRepresentation)
                        val userId = response.location.path.replace(".*/([^/]+)$".toRegex(), "$1")
                        Mono.just(user.copy(id = UserId(userId)))
                    }
                }
        } catch (exception: BusinessRuleValidationException) {
            log.error(
                "Error creating user with email: {} and username: {}",
                user.email.value,
                user.username.value,
                exception
            )
            when (exception) {
                is UserStoreException -> Mono.error(exception)
                is CredentialException -> Mono.error(
                    UserStoreException("Error creating user with email: ${user.email.value}", exception)
                )

                is UserException -> Mono.error(
                    UserStoreException("Error creating user with email: ${user.email.value}", exception)
                )

                else -> Mono.error(UserStoreException("Error creating user with email: ${user.email.value}", exception))
            }
        } catch (exception: ClientErrorException) {
            log.error("Error creating user with email: {}", user.email.value, exception)
            Mono.error(UserStoreException("Error creating user with email: ${user.email.value}", exception))
        }
    }

    /**
     * Checks if a user already exists.
     *
     * @param user the user to check
     * @return a Mono that emits a Boolean indicating whether the user already exists or not
     */
    private suspend fun checkIfUserAlreadyExists(user: User): Mono<Boolean> {
        val userByEmail = getUserByEmail(user.email.value)
        val userByUsername = getUserByUsername(user.username.value)

        return when {
            userByEmail != null || userByUsername != null -> Mono.just(true)
            else -> Mono.just(false)
        }
    }

    /**
     * Generates a UserRepresentation object based on the given User and CredentialRepresentation.
     *
     * @param user the User object containing the user information.
     * @param credentialRepresentation the [CredentialRepresentation] object containing the user credential information.
     * @return the generated [UserRepresentation] object.
     */
    private fun getUserRepresentation(
        user: User,
        credentialRepresentation: CredentialRepresentation
    ) = UserRepresentation().apply {
        username = user.username.value
        email = user.email.value
        firstName = user.name?.firstName?.value
        lastName = user.name?.lastName?.value
        isEnabled = true
        groups = listOf(USER_GROUP_NAME)
        credentials = listOf(credentialRepresentation)
    }

    /**
     * Retrieves a user by email from Keycloak realm.
     *
     * @param email The email of the user to retrieve.
     * @return The [UserRepresentation] object of the user with the specified email, or null if not found.
     */
    private fun getUserByEmail(email: String): UserRepresentation? =
        keycloakRealm.users().searchByEmail(email, true).firstOrNull()

    /**
     * Retrieves a user by their username.
     *
     * @param username The username of the user to retrieve.
     * @return The [UserRepresentation] object representing the user, or null if no user was found.
     */
    private fun getUserByUsername(username: String): UserRepresentation? =
        keycloakRealm.users().searchByUsername(username, true).firstOrNull()

    /**
     * Lazy initialization of the Keycloak realm.
     * The realm is retrieved from the Keycloak instance by accessing the application security properties.
     *
     * @return The Keycloak realm resource.
     * @see ApplicationSecurityProperties for more information about the application security properties.
     * @see Keycloak for more information about the Keycloak instance.
     */
    private val keycloakRealm by lazy {
        keycloak.realm(applicationSecurityProperties.oauth2.realm)
    }

    /**
     * Verifies a user by sending a verification email.
     *
     * @param userId the ID of the user to be verified
     */
    suspend fun verify(userId: String) {
        log.info("Verifying user with id: {}", userId)
        try {
            keycloakRealm.users()[userId].sendVerifyEmail()
        } catch (_: WebApplicationException) {
            log.error("Error sending email verification to user with id: {}", userId)
        }
    }

    companion object {
        private const val USER_GROUP_NAME = "Users"
        private val log = LoggerFactory.getLogger(KeycloakRepository::class.java)
    }
}
