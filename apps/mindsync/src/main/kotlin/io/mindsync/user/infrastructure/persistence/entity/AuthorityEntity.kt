package io.mindsync.user.infrastructure.persistence.entity

import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node

@Node("authorities")
class AuthorityEntity (
    @Id
    val name: String
)
