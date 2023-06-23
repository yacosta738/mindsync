plugins {
    idea
    id("sonarqube-conventions")
    id("jacoco-report-aggregation")
    jacoco
}

idea {
    module.isDownloadJavadoc = true
    module.isDownloadSources = true
}

// add tasks for all subprojects
subprojects {
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    tasks.withType<Copy> {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    tasks.withType<Jar> {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}

tasks.withType<Test> {
    finalizedBy("jacocoTestReport")
}

tasks.withType<JacocoReport> {
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(true)
    }
}
