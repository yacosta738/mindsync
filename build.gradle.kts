plugins {
    idea
    id("org.jetbrains.dokka")
    // due to late-binding not working, aggregation should define tasks doc-consumer
    id("aggregation-conventions")

    id("io.mindsync.verification.jacoco-consumer-conventions")
    id("io.mindsync.verification.test-consumer-conventions")
    id("io.mindsync.documentation.documentation-consumer-conventions")

    id("io.mindsync.verification.sonarqube-conventions")
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

// this task generates all tasks for sub-projects itself, therefor it just needs
// to be applied on the root project, conventions are not working :-(
tasks.dokkaHtmlMultiModule.configure {
    outputDirectory.set(buildDir.resolve("dokka"))
}

dependencies {
    asciidoc(project(":documentation"))
}
