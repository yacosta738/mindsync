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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class KeycloakRepository(
    private val applicationSecurityProperties: ApplicationSecurityProperties,
    private val keycloak: Keycloak
) : UserCreator<User> {

    override suspend fun create(user: User): Mono<Either<UserStoreException, User>> {
        log.info("Saving user with email: {}", user.email.value)

        return try {
            val password =
                user.credentials.firstOrNull()?.credentialValue ?: Credential.generateRandomCredentialPassword()
            val credentialRepresentation = CredentialRepresentation().apply {
                type = CredentialRepresentation.PASSWORD
                value = password
            }

            val userByEmail = getUserByEmail(user.email.value)
            val userByUsername = getUserByUsername(user.username.value)
            if (userByEmail != null) {
                log.error("User with email: {} already exists", user.email.value)
                Mono.just(Either.Left(UserStoreException("User with email: ${user.email.value} already exists")))
            } else if (userByUsername != null) {
                log.error("User with username: {} already exists", user.username.value)
                Mono.just(Either.Left(UserStoreException("User with username: ${user.username.value} already exists")))
            } else {
                val userRepresentation = UserRepresentation().apply {
                    username = user.username.value
                    email = user.email.value
                    firstName = user.name?.firstName?.value
                    lastName = user.name?.lastName?.value
                    isEnabled = true
                    groups = listOf(USER_GROUP_NAME)
                    credentials = listOf(credentialRepresentation)
                }

                val response = keycloakRealm.users().create(userRepresentation)

                if (response.status != HttpStatus.CREATED.value()) {
                    log.error("Error saving user with email: {}", user.email.value)
                    Mono.just(
                        Either.Left(
                            UserStoreException(
                                "Error saving user with email: ${user.email.value}. Is possible that the user already exists"
                            )
                        )
                    )
                } else {
                    val userId = response.location.path.replace(".*/([^/]+)$".toRegex(), "$1")
                    sendEmailVerification(userId)
                    Mono.just(Either.Right(user.copy(id = UserId(userId))))
                }
            }
        } catch (exception: BusinessRuleValidationException) {
            log.error(
                "Error saving user with email: ${user.email.value} with cause: ${exception.cause} and message: ${exception.message}"
            )
            val businessRuleException = UserStoreException(user.email.value, exception)
            Mono.just(Either.Left(businessRuleException))
        }
    }

    private fun getUserByEmail(email: String): UserRepresentation? =
        keycloakRealm.users().searchByEmail(email, true).firstOrNull()

    private fun getUserByUsername(username: String): UserRepresentation? =
        keycloakRealm.users().searchByUsername(username, true).firstOrNull()

    private val keycloakRealm by lazy {
        keycloak.realm(applicationSecurityProperties.oauth2.realm)
    }

    private suspend fun sendEmailVerification(userId: String) {
        log.info("Sending email verification to user with id: {}", userId)
        keycloakRealm.users()[userId].sendVerifyEmail()
    }

    companion object {
        private const val USER_GROUP_NAME = "Users"
        private val log: Logger = LoggerFactory.getLogger(KeycloakRepository::class.java)
    }
}
