package io.mindsync.user.infrastructure.persistence.repository.standard

import io.mindsync.user.infrastructure.persistence.entity.UserEntity
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import org.springframework.stereotype.Repository

@Repository
interface StandardUserRepository : ReactiveNeo4jRepository<UserEntity, String>
