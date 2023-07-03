import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

plugins {
    id("java-conventions")
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
