package io.mindsync.users.infrastructure.service

import io.mindsync.UnitTest
import io.mindsync.authentication.domain.AuthoritiesConstants
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User

@UnitTest
class AccountResourceServiceTest {

    private lateinit var accountResourceService: AccountResourceService
    private val userDetails: MutableMap<String, Any> = mutableMapOf(
        "sub" to "test",
        "preferred_username" to "test",
        "email" to "test@localhost",
        "given_name" to "Test",
        "family_name" to "User",
        "roles" to listOf(AuthoritiesConstants.USER)
    )

    @BeforeEach
    fun setUp() {
        accountResourceService = AccountResourceService()
    }

    @Test
    fun `should get account information successfully by OAuth2AuthenticationToken`() {
        val authenticationToken = createMockOAuth2AuthenticationToken(userDetails)
        val userResponse = accountResourceService.getAccount(authenticationToken).block()
        assertEquals("test", userResponse?.username)
        assertEquals("test@localhost", userResponse?.email)
        assertEquals("Test", userResponse?.firstname)
        assertEquals("User", userResponse?.lastname)
        assertEquals(1, userResponse?.authorities?.size)
        assertEquals(AuthoritiesConstants.USER, userResponse?.authorities?.first())
    }

    @Test
    fun `should get account information successfully with multiple roles by OAuth2AuthenticationToken`() {
        userDetails["roles"] = listOf(AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN)
        val authenticationToken = createMockOAuth2AuthenticationToken(userDetails)
        val userResponse = accountResourceService.getAccount(authenticationToken).block()
        assertEquals("test", userResponse?.username)
        assertEquals("test@localhost", userResponse?.email)
        assertEquals("Test", userResponse?.firstname)
        assertEquals("User", userResponse?.lastname)
        assertEquals(2, userResponse?.authorities?.size)
        assertTrue(userResponse?.authorities?.contains(AuthoritiesConstants.USER) ?: false)
        assertTrue(userResponse?.authorities?.contains(AuthoritiesConstants.ADMIN) ?: false)
    }
    private fun createMockOAuth2AuthenticationToken(userDetails: Map<String, Any>): OAuth2AuthenticationToken {
        val authorities: Collection<GrantedAuthority> =
            listOf<GrantedAuthority>(SimpleGrantedAuthority(AuthoritiesConstants.ANONYMOUS))
        val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(
            "anonymous",
            "anonymous",
            authorities
        )
        usernamePasswordAuthenticationToken.details = userDetails
        val user: OAuth2User = DefaultOAuth2User(authorities, userDetails, "sub")
        return OAuth2AuthenticationToken(user, authorities, "oidc")
    }
}
