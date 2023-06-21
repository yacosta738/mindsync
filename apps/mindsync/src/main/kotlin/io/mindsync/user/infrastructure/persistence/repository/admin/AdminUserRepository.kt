package io.mindsync.user.infrastructure.persistence.repository.admin

import io.mindsync.user.infrastructure.persistence.entity.UserEntity
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import org.springframework.stereotype.Repository

@Repository
interface AdminUserRepository : ReactiveNeo4jRepository<UserEntity, String>
