package io.mindsync.user.infrastructure.persistence.entity

import jakarta.validation.constraints.Size
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship
import org.springframework.data.neo4j.core.support.UUIDStringGenerator
import java.time.LocalDateTime
import java.util.*

@Node("users")
class UserEntity(
    @Id @GeneratedValue(UUIDStringGenerator::class)
    var id: String? = null,

    @get:Size(min = 1, max = 20)
    var firstName: String? = "",
    @get:Size(min = 1, max = 20)
    var lastName: String? = "",

    var email: String? = "",

    var password: String? = "",

    @Relationship(type = "HAS_AUTHORITIES", direction = Relationship.Direction.OUTGOING)
    val authorities: Set<AuthorityEntity> = emptySet(),
    var locale: Locale? = Locale.ENGLISH,
    var status: String? = ""
    )
