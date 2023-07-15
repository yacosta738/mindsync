package io.mindsync.users.infrastructure.persistence.keycloak

import arrow.core.Either
import io.mindsync.authentication.infrastructure.ApplicationSecurityProperties
import io.mindsync.common.domain.error.BusinessRuleValidationException
import io.mindsync.users.domain.Credential
import io.mindsync.users.domain.User
import io.mindsync.users.domain.UserCreator
import io.mindsync.users.domain.UserId
import io.mindsync.users.domain.exceptions.UserStoreException
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import javax.ws.rs.WebApplicationException

/**
 * The KeycloakRepository class is a repository implementation that interacts with Keycloak
 * for user creation and verification.
 *
 * @author acosta
 * @created 1/7/23
 *
 * @param applicationSecurityProperties The application security properties containing the Keycloak realm information.
 * @param keycloak The Keycloak instance used for API calls.
 * @see UserCreator for more information about the user creator component.
 * @see User for more information about the user object.
 * @see UserId for more information about the user id object.
 * @see UserStoreException for more information about the user store exception.
 * @see Credential for more information about the credential object.
 * @see CredentialRepresentation for more information about the credential representation object.
 * @see UserRepresentation for more information about the user representation object.
 * @see Mono for more information about the mono object.
 * @see Either for more information about the either object.
 * @see WebApplicationException for more information about the web application exception object.
 * @see BusinessRuleValidationException for more information about the business rule validation exception object.
 */
@Repository
class KeycloakRepository(
    private val applicationSecurityProperties: ApplicationSecurityProperties,
    private val keycloak: Keycloak
) : UserCreator<User> {

    /**
     * Creates a new user with the given user object. The user object contains the user details.
     * The user is created in Keycloak.
     *
     * @param user The user to be created.
     * @return A Mono that emits Either<UserStoreException, User>. The either object contains either a Left value
     * with a UserStoreException if there was an error creating the user, or a Right value with the created user
     * if the operation was successful.
     */
    override suspend fun create(user: User): Mono<Either<UserStoreException, User>> {
        log.info("Saving user with email: {}", user.email.value)

        val errorSavingUser = "Error saving user with email: ${user.email.value}"
        return try {
            val password =
                user.credentials.firstOrNull()?.credentialValue ?: Credential.generateRandomCredentialPassword()
            val credentialRepresentation = CredentialRepresentation().apply {
                type = CredentialRepresentation.PASSWORD
                value = password
            }

            checkIfUserAlreadyExists(user)
                .flatMap { either ->
                    either.fold(
                        { Mono.just(Either.Left(it)) }
                    ) {
                        val userRepresentation = getUserRepresentation(user, credentialRepresentation)
                        try {
                            val response = keycloakRealm.users().create(userRepresentation)
                            val userId = response.location.path.replace(".*/([^/]+)$".toRegex(), "$1")
                            Mono.just(Either.Right(user.copy(id = UserId(userId))))
                        } catch (exception: WebApplicationException) {
                            log.error(
                                "$errorSavingUser with cause: ${exception.cause} and message: ${exception.message}"
                            )
                            val userStoreException = UserStoreException(user.email.value, exception)
                            Mono.just(Either.Left(userStoreException))
                        }
                    }
                }
        } catch (exception: BusinessRuleValidationException) {
            log.error("$errorSavingUser with cause: ${exception.cause} and message: ${exception.message}")
            val businessRuleException = UserStoreException(user.email.value, exception)
            Mono.just(Either.Left(businessRuleException))
        }
    }

    /**
     * Checks if the user already exists in the user store.
     *
     * @param user The user to check.
     * @return A Mono that emits Either a UserStoreException if the user already exists,
     * or the user object if it does not exist.
     */
    private suspend fun checkIfUserAlreadyExists(user: User): Mono<Either<UserStoreException, User>> {
        val userByEmail = getUserByEmail(user.email.value)
        val userByUsername = getUserByUsername(user.username.value)

        return when {
            userByEmail != null -> {
                log.error("User with email: {} already exists", user.email.value)
                Mono.just(Either.Left(UserStoreException("User with email: ${user.email.value} already exists")))
            }

            userByUsername != null -> {
                log.error("User with username: {} already exists", user.username.value)
                Mono.just(Either.Left(UserStoreException("User with username: ${user.username.value} already exists")))
            }

            else -> Mono.just(Either.Right(user))
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
