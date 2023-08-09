package io.mindsync.users.infrastructure.persistence.keycloak

import io.kotest.matchers.shouldBe
import io.mindsync.UnitTest
import io.mindsync.authentication.infrastructure.ApplicationSecurityProperties
import io.mindsync.users.domain.Credential
import io.mindsync.users.domain.User
import io.mindsync.users.domain.exceptions.UserStoreException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import jakarta.ws.rs.core.Response
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import net.datafaker.Faker
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.admin.client.resource.UsersResource
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

private const val REALM = "realm"
private const val SERVER_URL = "http://localhost/auth"
private const val CLIENT_ID = "client"
private const val CLIENT_SECRET = "secret"

@UnitTest
class KeycloakRepositoryTest {
    private val faker = Faker()
    private lateinit var applicationSecurityProperties: ApplicationSecurityProperties
    private lateinit var keycloak: Keycloak
    private lateinit var keycloakRealm: RealmResource
    private lateinit var keycloakUserResource: UsersResource
    private lateinit var keycloakRepository: KeycloakRepository

    @BeforeEach
    fun setUp() {
        applicationSecurityProperties = mockk<ApplicationSecurityProperties>()
        every { applicationSecurityProperties.oauth2.realm } returns REALM
        every { applicationSecurityProperties.oauth2.serverUrl } returns SERVER_URL
        every { applicationSecurityProperties.oauth2.clientId } returns CLIENT_ID
        every { applicationSecurityProperties.oauth2.clientSecret } returns CLIENT_SECRET

        keycloak = mockk()
        keycloakRealm = mockk()
        keycloakUserResource = mockk()
        coEvery { keycloak.realm(any()) } returns keycloakRealm
        coEvery { keycloakRealm.users() } returns keycloakUserResource

        keycloakRepository = KeycloakRepository(applicationSecurityProperties, keycloak)
    }

    @Test
    fun `should create user and return user with ID`() = runBlocking {
        // Arrange
        val user = createUser()
        val userRepresentation = createUserRepresentation(user)
        val response = createResponse(userRepresentation)

        coEvery { keycloakUserResource.searchByEmail(any(), any()) } returns listOf()
        coEvery { keycloakUserResource.searchByUsername(any(), any()) } returns listOf()
        coEvery { keycloakUserResource.create(any()) } returns response

        // Act
        val result: Mono<User> = keycloakRepository.create(user)
        val createdUser = result.awaitSingle()

        // Assert
        createdUser.id.value shouldBe user.id.value

        // Verify that the expected methods were called
        coVerify { keycloakUserResource.searchByEmail(any(), eq(true)) }
        coVerify { keycloakUserResource.searchByUsername(any(), eq(true)) }
        coVerify { keycloakUserResource.create(any()) }
    }

    @Test
    fun `should not create user if email already exists`() = runBlocking {
        // Arrange
        val user = createUser()
        val userRepresentation = createUserRepresentation(user)
        val response = createResponse(userRepresentation)

        coEvery { keycloakUserResource.searchByEmail(any(), any()) } returns listOf(userRepresentation)
        coEvery { keycloakUserResource.searchByUsername(any(), any()) } returns listOf()
        coEvery { keycloakUserResource.create(any()) } returns response

        // Act
        val resultMono = keycloakRepository.create(user)

        StepVerifier.create(resultMono)
            .expectError(UserStoreException::class.java)
            .verifyThenAssertThat()

        // Verify that the expected methods were called
        coVerify { keycloakUserResource.searchByEmail(any(), eq(true)) }
        coVerify(exactly = 0) { keycloakUserResource.create(any()) }
    }

    @Test
    fun `should not create user if username already exists`() = runBlocking {
        // Arrange
        val user = createUser()
        val userRepresentation = createUserRepresentation(user)
        val response = createResponse(userRepresentation)

        coEvery { keycloakUserResource.searchByEmail(any(), any()) } returns listOf()
        coEvery { keycloakUserResource.searchByUsername(any(), any()) } returns listOf(userRepresentation)
        coEvery { keycloakUserResource.create(any()) } returns response

        // Act
        val resultMono = keycloakRepository.create(user)

        StepVerifier.create(resultMono)
            .expectError(UserStoreException::class.java)
            .verifyThenAssertThat()

        // Verify that the expected methods were called
        coVerify { keycloakUserResource.searchByEmail(any(), eq(true)) }
        coVerify { keycloakUserResource.searchByUsername(any(), eq(true)) }
        coVerify(exactly = 0) { keycloakUserResource.create(any()) }
    }

    @Test
    fun `should not create user if keycloak returns error`() = runBlocking {
        // Arrange
        val user = createUser()

        coEvery { keycloakUserResource.searchByEmail(any(), any()) } returns listOf()
        coEvery { keycloakUserResource.searchByUsername(any(), any()) } returns listOf()
        coEvery {
            keycloakUserResource.create(any())
        } throws UserStoreException("Error creating user with email: ${user.email.value}")

        // Act
        val resultMono = keycloakRepository.create(user)

        StepVerifier.create(resultMono)
            .expectError(UserStoreException::class.java)
            .verifyThenAssertThat()

        // Verify that the expected methods were called
        coVerify { keycloakUserResource.searchByEmail(any(), eq(true)) }
        coVerify { keycloakUserResource.searchByUsername(any(), eq(true)) }
        coVerify { keycloakUserResource.create(any()) }
    }

    private fun createUser(
        email: String = faker.internet().emailAddress(),
        firstName: String = faker.name().firstName(),
        lastName: String = faker.name().lastName(),
        password: String = faker.internet().password(8, 80, true, true, true),
    ): User = User.create(email, firstName, lastName, password)

    private fun createUserRepresentation(user: User): UserRepresentation {
        val userRepresentation = UserRepresentation()
        userRepresentation.id = user.id.value.toString()
        userRepresentation.email = user.email.value
        userRepresentation.firstName = user.name?.firstName?.value
        userRepresentation.lastName = user.name?.lastName?.value
        userRepresentation.isEnabled = true
        userRepresentation.isEmailVerified = true
        userRepresentation.credentials = createCredentialRepresentation(user.credentials)
        return userRepresentation
    }

    private fun createCredentialRepresentation(credentials: MutableList<Credential>): List<CredentialRepresentation> {
        return credentials.map {
            val credentialRepresentation = CredentialRepresentation()
            credentialRepresentation.type = CredentialRepresentation.PASSWORD
            credentialRepresentation.value = it.value
            credentialRepresentation.isTemporary = false
            credentialRepresentation
        }
    }

    private fun createResponse(userRepresentation: UserRepresentation): Response {
        val userId = userRepresentation.id
        val responsePath = "/auth/admin/realms/$REALM/users/$userId"
        return Response.status(201).header("Location", responsePath).build()
    }
}
