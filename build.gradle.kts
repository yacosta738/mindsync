plugins {
    idea
    id("org.jetbrains.dokka")
    // due to late-binding not working, aggregation should define tasks doc-consumer
    id("aggregation-conventions")
    id("testing-conventions")
    id("io.mindsync.verification.test-consumer-conventions")
    id("io.mindsync.documentation.documentation-consumer-conventions")
    id("io.mindsync.verification.sonarqube-conventions")
    id("com.gorylenko.gradle-git-properties")
}

idea {
    module.isDownloadJavadoc = true
    module.isDownloadSources = true
    // exclude node_modules from indexing
    module.excludeDirs.add(file("**/node_modules"))
}

gitProperties {
    failOnNoGitDirectory = false
    keys = listOf("git.branch", "git.commit.id.abbrev", "git.commit.id.describe")
}


allprojects {
    group = "io.mindsync.gradle"
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
