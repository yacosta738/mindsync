package io.mindsync.verification

import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    kotlin("jvm")
    jacoco
    id("jacoco-report-aggregation")
}

val libs: VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

jacoco {
    toolVersion = libs.findVersion("jacoco").get().requiredVersion
}
tasks.named<JacocoReport>("jacocoTestReport") {
    reports {
        xml.required.set(true)
        csv.required.set(true)
        html.required.set(true)
    }
}
tasks.check {
    dependsOn(tasks.named<JacocoReport>("testCodeCoverageReport"))
}
