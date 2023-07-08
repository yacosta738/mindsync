package io.mindsync.common.domain

/**
 * An abstract class representing an aggregate root entity.
 *
 * @param ID the data type of the aggregate root's identifier
 *
 * @see BaseEntity
 * @author Yuniel Acosta
 */
@Suppress("unused")
abstract class AggregateRoot<ID> : BaseEntity<ID>()
