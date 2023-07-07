package io.mindsync.users.infrastructure.persistence.keycloak

import arrow.core.Either
import io.mindsync.authentication.infrastructure.ApplicationSecurityProperties
import io.mindsync.common.domain.error.BusinessRuleValidationException
import io.mindsync.users.domain.*
import io.mindsync.users.domain.exceptions.UserStoreException
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import javax.ws.rs.WebApplicationException

@Repository
class KeycloakRepository(
    private val applicationSecurityProperties: ApplicationSecurityProperties,
    private val keycloak: Keycloak
) : UserCreator<User> {

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

    private fun getUserByEmail(email: String): UserRepresentation? =
        keycloakRealm.users().searchByEmail(email, true).firstOrNull()

    private fun getUserByUsername(username: String): UserRepresentation? =
        keycloakRealm.users().searchByUsername(username, true).firstOrNull()

    private val keycloakRealm by lazy {
        keycloak.realm(applicationSecurityProperties.oauth2.realm)
    }

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
        private val log: Logger = LoggerFactory.getLogger(KeycloakRepository::class.java)
    }
}
