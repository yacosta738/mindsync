package io.mindsync.verification

plugins {
    kotlin("jvm")
}

val testReportData: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
    extendsFrom(configurations.implementation.get())
    attributes {
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
        attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named("test-report-data"))
    }
}

val testReportTask = tasks.register<TestReport>("testReport") {
    destinationDirectory.set(file("${layout.buildDirectory}/reports/allTests"))
    // Use test results from testReportData configuration
    (testResults as ConfigurableFileCollection)
        .from(testReportData.incoming.artifactView { lenient(true) }.files)
}

tasks.check {
    dependsOn(testReportTask)
}
