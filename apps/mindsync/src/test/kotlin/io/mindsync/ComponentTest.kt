package io.mindsync

import org.junit.jupiter.api.DisplayNameGeneration
import java.lang.annotation.Inherited

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@DisplayNameGeneration(ReplaceCamelCase::class)
@Inherited
annotation class ComponentTest
