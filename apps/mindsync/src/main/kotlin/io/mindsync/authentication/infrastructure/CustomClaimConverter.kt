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

/**
 * CustomClaimConverter is a class that implements the [Converter] interface to convert a map of claims
 * to a modified map of claims by appending custom claims from a user object.
 *
 * @param registration The client registration details.
 * @param webClient The WebClient instance used to make HTTP requests.
 * @param token The token used for authentication.
 * @since 1.0.0
 * @author Yuniel Acosta
 */
class CustomClaimConverter(
    private val registration: ClientRegistration,
    private val webClient: WebClient,
    private val token: String
) : Converter<Map<String, Any>, Map<String, Any>> {

    private val delegate = MappedJwtClaimSetConverter.withDefaults(emptyMap())
    private val users: (SubAttributes) -> Mono<ObjectNode> = Memoizers.of { _ ->
        loadUser()
    }

    /**
     * Appends custom claims from the user object to the claim map.
     *
     * @param claim The claim map to append the custom claims to.
     * @param user The user object containing the custom claims.
     * @return The updated claim map after appending the custom claims.
     */
    private fun appendCustomClaim(claim: MutableMap<String, Any>, user: ObjectNode): MutableMap<String, Any> {
        CLAIM_APPENDERS.stream().forEach {
            it.append(claim, user)
        }.apply { return claim }
    }

    /**
     * Converts the given claims map into a new map with additional custom claims.
     *
     * @param claims The original claims map to be converted.
     * @return The converted claims map with additional custom claims.
     */
    override fun convert(claims: Map<String, Any>): Map<String, Any> {
        val convertedClaims = delegate.convert(claims)?.toMutableMap() ?: mutableMapOf()
        val user = getUser(claims).block()
        return user?.let { appendCustomClaim(convertedClaims, it) } ?: convertedClaims
    }

    /**
     * Retrieves user information based on the claims provided.
     *
     * @param claims A map containing claims associated with the user.
     * @return A Mono emitting an ObjectNode representing the user information,
     *         or an empty Mono if the 'sub' claim is not present or is not a String.
     */
    private fun getUser(claims: Map<String, Any>): Mono<ObjectNode> {
        val sub = claims[SUB] as? String ?: return Mono.empty()
        val subAttributes = SubAttributes(sub)
        return users(subAttributes)
    }

    /**
     * Loads the user information from the provider's user info endpoint.
     *
     * @return A Mono that emits the user information as a JSON object.
     */
    private fun loadUser(): Mono<ObjectNode> {
        return getToken().flatMap { token ->

            webClient.get()
                .uri(registration.providerDetails.userInfoEndpoint.uri)
                .headers { it.setBearerAuth(token) }
                .retrieve()
                .bodyToMono(ObjectNode::class.java)
        }
    }

    /**
     * Retrieves the token.
     *
     * @return A Mono that emits the token as a String.
     */
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

        /**
         * Builds a list of ClaimAppenders.
         *
         * @return List of ClaimAppenders that will be used for adding claims to the token.
         */
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

        /**
         * Builds a list of strings from a given JsonNode.
         *
         * @param node the JsonNode from which the list of strings will be built.
         * @return a list of strings extracted from the JsonNode.
         */
        private fun buildList(node: JsonNode): List<String> {
            return StreamSupport.stream(node.spliterator(), false)
                .map(JsonNode::asText)
                .toList()
        }
    }

    /**
     * This interface represents a ClaimAppender, which is responsible for appending claims to a MutableMap
     * using the provided user ObjectNode.
     */
    private fun interface ClaimAppender {
        /**
         * Appends the given user object to the claim map.
         *
         * @param claim The claim map to append the user object to.
         * @param user The user object to append to the claim map.
         */
        fun append(claim: MutableMap<String, Any>, user: ObjectNode)
    }

    /**
     * A implementation of the ClaimAppender interface that appends a claim to a user object based on a given key.
     *
     * @param key The key used to retrieve the value from the user object.
     */
    private class StandardClaimAppender(private val key: String) : ClaimAppender {
        /**
         * Appends the given user object to the claim map.
         *
         * @param claim The claim map to append the user object to.
         * @param user The user object to append to the claim map.
         */
        override fun append(claim: MutableMap<String, Any>, user: ObjectNode) {
            if (user.has(key)) {
                claim[key] = user[key].asText()
            }
        }
    }

    /**
     * A class that implements the ClaimAppender interface and appends a name claim to a mutable map of claims.
     */
    private class NameClaimAppender : ClaimAppender {
        /**
         * Appends the user's name to a claim.
         *
         * This method allows appending the user's full name to a claim. If the user object contains a `name` field,
         * the field's value will be split into given and family names and appended to the claim.
         *
         * @param claim The claim to append the user's name to. The claim should be a mutable map with String keys and Any values.
         * @param user The user object containing the user's information. The user object should be of type ObjectNode.
         */
        override fun append(claim: MutableMap<String, Any>, user: ObjectNode) {
            // Allow full name in a name claim - happens with Auth0
            if (user.has(NAME)) {
                val name =
                    user[NAME].asText().trim().split("\\s+".toRegex()).filter { it.isNotBlank() }.toTypedArray()

                if (name.isNotEmpty()) {
                    claim[GIVEN_NAME] = name[0]
                    claim[FAMILY_NAME] = name.copyOfRange(1, name.size).joinToString(" ")
                }
            }
        }
    }

    /**
     * A class responsible for appending group information to a claim.
     */
    private class GroupClaimAppender : ClaimAppender {
        /**
         * Appends the value of the "GROUPS" attribute from the given user object to the claim map.
         * If the user object has the "GROUPS" attribute, the value is extracted and added to the claim map.
         *
         * @param claim The claim map to which the "GROUPS" value will be appended.
         * @param user The user object from which the "GROUPS" value will be extracted.
         */
        override fun append(claim: MutableMap<String, Any>, user: ObjectNode) {
            if (user.has(GROUPS)) {
                val groups = buildList(user[GROUPS])

                claim[GROUPS] = groups
            }
        }
    }

    /**
     * A private class that appends roles to a given claim.
     */
    private class RolesClaimAppender : ClaimAppender {
        /**
         * Appends roles from the user object to the claim object.
         *
         * @param claim The claim map to append the roles to.
         * @param user The user object that contains the roles.
         */
        override fun append(claim: MutableMap<String, Any>, user: ObjectNode) {
            if (user.has(Claims.CLAIMS_NAMESPACE + ROLES)) {
                val roles = buildList(user.get(Claims.CLAIMS_NAMESPACE + ROLES))

                claim[ROLES] = roles
            }
        }
    }

    /**
     * Represents a class that holds sub attributes.
     * @property sub The sub attribute.
     */
    private data class SubAttributes(val sub: String)
}
