package io.mindsync.verification

import org.sonarqube.gradle.SonarTask

plugins {
    id("java-conventions")
    id("org.sonarqube")
}

val githubOrg: String by project
val githubProjectUrl = "https://github.com/$githubOrg/${rootProject.name}"

sonar {
    properties {
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.projectName", rootProject.name)
        property("sonar.projectKey", System.getenv()["SONAR_PROJECT_KEY"] ?: "yacosta738_mindsync")
        property("sonar.organization", System.getenv()["SONAR_ORGANIZATION"] ?: "yacosta738")
        property("sonar.projectVersion", rootProject.version.toString())
        property("sonar.host.url", System.getenv()["SONAR_HOST_URL"] ?: "https://sonarcloud.io")
        property("sonar.login", System.getenv()["SONAR_TOKEN"] ?: "")
        property("sonar.scm.provider", "git")
        property("sonar.links.homepage", githubProjectUrl)
        property("sonar.links.ci", "$githubProjectUrl/actions")
        property("sonar.links.scm", githubProjectUrl)
        property("sonar.links.issue", "$githubProjectUrl/issues")
    }
}

subprojects {
    sonar {
        properties {
            if (!project.name.equals("mindsync-frontend", ignoreCase = true)) {
                property(
                    "sonar.coverage.jacoco.xmlReportPaths",
                    // current project build dir
                    layout.buildDirectory.file("reports/jacoco/testCodeCoverageReport/testCodeCoverageReport.xml").get().asFile.absolutePath
                )
            }
        }
    }
}

tasks.withType<SonarTask>().configureEach {
    onlyIf {
        println("Running sonarqube for ${project.name}")
        file("src/main").exists()
    }
    dependsOn(project.tasks.named("testCodeCoverageReport"))
}
