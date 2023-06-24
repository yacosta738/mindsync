plugins {
    id("kotlin-conventions")
    id("testing-conventions")
    id("dokka-conventions")
    id("sonarqube-conventions")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
}
