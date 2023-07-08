package io.mindsync.common.domain

/**
 * A base class for entities with a generic identifier.
 *
 * @param ID the type of the identifier
 * @property id The unique identifier of the entity.
 * @author Yuniel Acosta
 */
abstract class BaseEntity<ID> {
    abstract val id: ID
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BaseEntity<*>

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
