plugins {
    id("org.sonarqube")
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
            property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
            property(
                "sonar.exclusions",
                "**/node_modules/**,**/src/test/**,**/src/main/resources/**,**/src/test/resources/**"
            )
        }
    }
}
