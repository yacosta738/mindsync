package io.mindsync.authentication.infrastructure

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import io.mindsync.common.domain.Memoizers
import org.springframework.core.convert.converter.Converter
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.jwt.MappedJwtClaimSetConverter
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.stream.StreamSupport

class CustomClaimConverter(
    private val registration: ClientRegistration,
    private val webClient: WebClient,
    private val token: String
) : Converter<Map<String, Any>, Map<String, Any>> {

    private val delegate = MappedJwtClaimSetConverter.withDefaults(emptyMap())
    private val users: (SubAttributes) -> Mono<ObjectNode> = Memoizers.of { _ ->
        loadUser()
    }

    private fun appendCustomClaim(claim: MutableMap<String, Any>, user: ObjectNode): MutableMap<String, Any> {
        CLAIM_APPENDERS.stream().forEach {
            it.append(claim, user)
        }.apply { return claim }
    }

    override fun convert(claims: Map<String, Any>): Map<String, Any> {
        val convertedClaims = delegate.convert(claims)?.toMutableMap() ?: mutableMapOf()
        val user = getUser(claims).block()
        return user?.let { appendCustomClaim(convertedClaims, it) } ?: convertedClaims
    }

    private fun getUser(claims: Map<String, Any>): Mono<ObjectNode> {
        val sub = claims[SUB] as? String ?: return Mono.empty()
        val subAttributes = SubAttributes(sub)
        return users(subAttributes)
    }

    private fun loadUser(): Mono<ObjectNode> {
        return getToken().flatMap { token ->

            webClient.get()
                .uri(registration.providerDetails.userInfoEndpoint.uri)
                .headers { it.setBearerAuth(token) }
                .retrieve()
                .bodyToMono(ObjectNode::class.java)
        }
    }

    private fun getToken(): Mono<String> {
        return Mono.just(token)
    }

    private companion object {
        private const val GIVEN_NAME = "given_name"
        private const val FAMILY_NAME = "family_name"
        private const val EMAIL = "email"
        private const val GROUPS = "groups"
        private const val NAME = "name"
        private const val PREFERRED_USERNAME = "preferred_username"
        private const val ROLES = "roles"
        private const val SUB = "sub"

        private val CLAIM_APPENDERS = buildAppenders()

        private fun buildAppenders(): List<ClaimAppender> {
            return listOf(
                StandardClaimAppender(PREFERRED_USERNAME),
                StandardClaimAppender(GIVEN_NAME),
                StandardClaimAppender(FAMILY_NAME),
                StandardClaimAppender(EMAIL),
                NameClaimAppender(),
                GroupClaimAppender(),
                RolesClaimAppender()
            )
        }

        private fun buildList(node: JsonNode): List<String> {
            return StreamSupport.stream(node.spliterator(), false)
                .map(JsonNode::asText)
                .toList()
        }
    }

    private interface ClaimAppender {
        fun append(claim: MutableMap<String, Any>, user: ObjectNode)
    }

    private class StandardClaimAppender(private val key: String) : ClaimAppender {
        override fun append(claim: MutableMap<String, Any>, user: ObjectNode) {
            if (user.has(key)) {
                claim[key] = user.get(key).asText()
            }
        }
    }

    private class NameClaimAppender : ClaimAppender {
        override fun append(claim: MutableMap<String, Any>, user: ObjectNode) {
            // Allow full name in a name claim - happens with Auth0
            if (user.has(NAME)) {
                val name =
                    user.get(NAME).asText().trim().split("\\s+".toRegex()).filter { it.isNotBlank() }.toTypedArray()

                if (name.isNotEmpty()) {
                    claim[GIVEN_NAME] = name[0]
                    claim[FAMILY_NAME] = name.copyOfRange(1, name.size).joinToString(" ")
                }
            }
        }
    }

    private class GroupClaimAppender : ClaimAppender {
        override fun append(claim: MutableMap<String, Any>, user: ObjectNode) {
            if (user.has(GROUPS)) {
                val groups = buildList(user.get(GROUPS))

                claim[GROUPS] = groups
            }
        }
    }

    private class RolesClaimAppender : ClaimAppender {
        override fun append(claim: MutableMap<String, Any>, user: ObjectNode) {
            if (user.has(Claims.CLAIMS_NAMESPACE + ROLES)) {
                val roles = buildList(user.get(Claims.CLAIMS_NAMESPACE + ROLES))

                claim[ROLES] = roles
            }
        }
    }

    private data class SubAttributes(val sub: String)
}
