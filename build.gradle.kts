plugins {
    idea
    id("sonarqube-conventions")
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
