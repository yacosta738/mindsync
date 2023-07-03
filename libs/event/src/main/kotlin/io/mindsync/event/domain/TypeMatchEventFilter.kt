package io.mindsync.event.domain

import kotlin.reflect.KClass

class TypeMatchEventFilter<T : Any>(
    private val clazz: KClass<T>
) : EventFilter<T> {
    override suspend fun filter(event: T): Boolean {
        return clazz.isInstance(event)
    }
}
