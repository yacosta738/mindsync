package io.mindsync.common.domain


abstract class BaseId<T> protected constructor(val value: T) {
    init {
        require(value != null) { "The id cannot be null" }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseId<*>) return false

        return value == other.value
    }

    override fun hashCode(): Int {
        return value?.hashCode() ?: 0
    }
}