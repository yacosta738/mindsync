package io.mindsync.verification

import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    kotlin("jvm")
    id("idea")
}

// -----------------------------
// Add testIntegration Sourceset and Task (and also add those as Test-Module to IDEA)
// -----------------------------

// to get rid of "Overload resolution ambiguity"-messsage
val sourceSets = project.extensions.getByType(SourceSetContainer::class)

// -----------------------------
// Add configuration to allow aggregation of unit-test-reports
// -----------------------------

// Share the test report data to be aggregated for the whole project
configurations.create("binaryTestResultsElements") {
    isVisible = false
    isCanBeResolved = false
    isCanBeConsumed = true
    extendsFrom(configurations.implementation.get())
    attributes {
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
        attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named("test-report-data"))
    }

    outgoing.artifact(tasks.test.map { task -> task.binaryResultsDirectory.get() })
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        useJUnitPlatform()
        events = setOf(
            TestLogEvent.FAILED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED,
            TestLogEvent.STANDARD_OUT
        )
        exceptionFormat = TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}
