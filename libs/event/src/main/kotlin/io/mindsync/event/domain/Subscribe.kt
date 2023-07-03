package io.mindsync.event.domain

import kotlin.reflect.KClass

annotation class Subscribe(
    val filterBy: KClass<*>
)
