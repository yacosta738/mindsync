package io.mindsync

import io.mindsync.authentication.infrastructure.TestSecurityConfiguration
import org.junit.jupiter.api.DisplayNameGeneration
import org.junit.jupiter.api.Tag
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.annotation.AliasFor
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import java.lang.annotation.Inherited
import kotlin.reflect.KClass

@WithMockUser
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@DisplayNameGeneration(ReplaceCamelCase::class)
@Inherited
@Tag("integration")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [MindsyncApp::class, TestSecurityConfiguration::class]
)
@ActiveProfiles("test")
annotation class IntegrationTest(@get:AliasFor(annotation = SpringBootTest::class) val properties: Array<String> = [])
