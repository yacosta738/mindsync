import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED

val libs: VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

plugins {
    id("java-conventions")
    // the following conventions depend on each other, keep them in the following order
    id("io.mindsync.verification.test-producer-conventions")
    id("io.mindsync.verification.jacoco-producer-conventions")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events = setOf(FAILED)
        exceptionFormat = FULL
    }
}

dependencies {
    add("testImplementation", libs.findBundle("junit").get())
    add("testImplementation", libs.findLibrary("mockk").get())
    add("testImplementation", libs.findLibrary("datafaker").get())
    add("testImplementation", libs.findLibrary("reactor-test").get())
    add("testImplementation", libs.findBundle("kotest").get())
    add("testImplementation", libs.findBundle("rest-assured").get())
    add("testImplementation", libs.findLibrary("cucumber-java").get())
}
