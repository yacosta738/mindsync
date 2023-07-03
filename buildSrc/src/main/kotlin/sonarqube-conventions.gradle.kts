val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

plugins {
    id("java-conventions")
    id("org.sonarqube")
    id("jacoco-report-aggregation")
    id("jacoco")
}

sonarqube {
    properties {
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.projectKey", "yacosta738_mindsync")
        property("sonar.organization", "yacosta738")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}
subprojects {
    sonar {
        properties {
            property("sonar.sources", "src")
            property("sonar.tests", "src/test")
            property("sonar.java.binaries", "build/classes/kotlin/main")
            property("sonar.java.test.binaries", "build/classes/kotlin/test")
            property("sonar.junit.reportPaths", "build/test-results/test")
            property("sonar.jacoco.reportPaths", "build/jacoco/test.exec")
            property(
                "sonar.coverage.jacoco.xmlReportPaths",
                "${projectDir.parentFile.path}/build/reports/jacoco/codeCoverageReport/codeCoverageReport.xml"
            )
            property(
                "sonar.exclusions",
                "**/node_modules/**,**/src/test/**,**/src/main/resources/**,**/src/test/resources/**"
            )
        }
    }
}

//jacoco {
//    toolVersion = libs.findVersion("jacoco").get().strictVersion
//}

tasks.withType<Test> {
    finalizedBy("codeCoverageReport")
}
// task to gather code coverage from multiple subprojects
// NOTE: the `JacocoReport` tasks do *not* depend on the `test` task by default. Meaning you have to ensure
// that `test` (or other tasks generating code coverage) run before generating the report.
// You can achieve this by calling the `test` lifecycle task manually
// $ ./gradlew test codeCoverageReport
tasks.register<JacocoReport>("codeCoverageReport") {
    // If a subproject applies the 'jacoco' plugin, add the result it to the report
    subprojects {
        val subproject = this
        subproject.plugins.withType<JacocoPlugin>().configureEach {
            subproject.tasks.matching { it.extensions.findByType<JacocoTaskExtension>() != null }.configureEach {
                if (this.extensions.getByType(JacocoTaskExtension::class).isEnabled) {
                    val testTask = this
                    sourceSets(subproject.sourceSets.getByName("main"))
                    executionData(testTask)
                } else {
                    logger.warn(
                        "Jacoco extension is disabled for test task ${this.name} in project ${subproject.name}. this test task will be excluded from jacoco report."
                    )
                }
            }

            // To automatically run `test` every time `./gradlew codeCoverageReport` is called,
            // you may want to set up a task dependency between them as shown below.
            // Note that this requires the `test` tasks to be resolved eagerly (see `forEach`) which
            // may have a negative effect on the configuration time of your build.
            subproject.tasks.matching { it.extensions.findByType<JacocoTaskExtension>() != null }.forEach {
                rootProject.tasks["codeCoverageReport"].dependsOn(it)
            }
        }
    }

    // enable the different report types (html, xml, csv)
    reports {
        // xml is usually used to integrate code coverage with
        // other tools like SonarQube, Coveralls or Codecov
        xml.required.set(true)

        // HTML reports can be used to see code coverage
        // without any external tools
        html.required.set(true)

        // CSV reports can be used to integrate code coverage with
        // other tools like SonarQube, Coveralls or Codecov
        csv.required.set(true)
    }
}
