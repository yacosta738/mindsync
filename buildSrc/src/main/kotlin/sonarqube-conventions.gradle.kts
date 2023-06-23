plugins {
    id("org.sonarqube")
}

sonarqube {
    properties {
        property("sonar.projectKey", "yacosta738_mindsync")
        property("sonar.organization", "yacosta738")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}
